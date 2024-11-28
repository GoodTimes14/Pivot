package eu.magicmine.pivot.api.redis;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.redis.cache.impl.RedisCache;
import eu.magicmine.pivot.api.redis.listener.LettuceMessageListener;
import eu.magicmine.pivot.api.redis.listener.RedisListener;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import eu.magicmine.pivot.api.utils.redis.RedisListen;
import eu.magicmine.pivot.api.utils.redis.RedisMethod;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.protocol.ProtocolVersion;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.Getter;
import org.apache.commons.pool2.impl.SoftReferenceObjectPool;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

@Getter
public class LettuceConnection implements IRedisConnection {

    private final Pivot pivot;
    private final RedisCache cache;
    private final Map<String, List<RedisMethod>> methodMap;
    private final ConnectionData connectionData;
    private RedisClient client;

    private SoftReferenceObjectPool<StatefulRedisConnection<String, String>> cachePool;

    private List<LettuceMessageListener> listeners = new ArrayList<>();

    private final Set<StatefulRedisConnection<String,String>> cacheConnections = new HashSet<>();

    private StatefulRedisPubSubConnection<String,String> subscribeConnection;
    private StatefulRedisPubSubConnection<String,String> publishConnection;



    public LettuceConnection(Pivot pivot, ConnectionData connectionData) {
        this.pivot = pivot;
        this.connectionData = connectionData;
        methodMap = new HashMap<>();

        cache = new RedisCache(this);
        connect(connectionData);
    }

    @Override
    public void connect(ConnectionData data) {
        RedisURI uri = RedisURI.create(data.getHost(),data.getPort());

        ClientResources res = DefaultClientResources.builder()
                .ioThreadPoolSize(8)
                .computationThreadPoolSize(4)
                .build();

        client = RedisClient.create(res,uri);

        client.setOptions(
                ClientOptions.builder()
                        .autoReconnect(true)
                        .protocolVersion(ProtocolVersion.RESP3).build());


        subscribeConnection = client.connectPubSub();
        publishConnection = client.connectPubSub();

//
//        channelPool = ConnectionPoolSupport
//                .createSoftReferenceObjectPool(client::connectPubSub, true);

        cachePool = ConnectionPoolSupport
                .createSoftReferenceObjectPool(client::connect, true);
    }

    @Override
    public long publish(String channel, String message) {
        return publishConnection.sync().publish(channel, message);
    }

    @Override
    public RedisFuture<Long> publishAsync(String channel, String message) {

        try {

            return publishConnection.async().publish(channel, message);

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while publishing message",exception);
        }

        return null;
    }


    @Override
    public void subscribe(String channel) {
        try {


            LettuceMessageListener lettuceMessageListener = new LettuceMessageListener(this,subscribeConnection);
            listeners.add(lettuceMessageListener);

            subscribeConnection.sync().subscribe(channel);
            subscribeConnection.addListener(lettuceMessageListener);

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while subscribing",exception);
        }
    }

    @Override
    public void hopperMessage(String channel, String message) {
        if(!methodMap.containsKey(channel)) {
            pivot.getLogger().log(Level.FINE,"Listeners not found");
            return;
        }

        for(RedisMethod redisMethod : methodMap.get(channel)) {
            try {
                redisMethod.getMethod().invoke(redisMethod.getHolder(),message);
            } catch (ReflectiveOperationException e) {
                pivot.getLogger().log(Level.SEVERE,"Can't invoke method: " + redisMethod.getMethod().getName(),e);
            }
        }

    }

    @Override
    public void registerListener(RedisListener listener) {
        for(Method method : listener.getClass().getMethods()) {
            if(method.isAnnotationPresent(RedisListen.class)) {
                RedisListen annotation = method.getAnnotation(RedisListen.class);
                if(method.getParameterTypes().length != 1) {
                    continue;
                }
                if(methodMap.containsKey(annotation.channel())) {
                    methodMap.get(annotation.channel()).add(new RedisMethod(listener,annotation,method));
                } else {
                    methodMap.put(annotation.channel(),new ArrayList<>(Collections.singletonList(new RedisMethod(listener,annotation,method))));
                }
            }
        }
    }


    public StatefulRedisConnection<String,String> getCacheConnection() throws Exception {

        try {

            StatefulRedisConnection<String,String> connection = cachePool.borrowObject();

            if(connectionData.isAuth()) {
                connection.sync().auth(connectionData.getPassword());
            }

            return connection;

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while getting CacheConnection" ,exception);
            return null;
        }
    }


//    public StatefulRedisPubSubConnection<String,String> getPubSubConnection() throws Exception {
//
//        try {
//
//            StatefulRedisPubSubConnection<String,String> connection = channelPool.borrowObject();
//
//            if(connectionData.isAuth()) {
//                connection.sync().auth(connectionData.getPassword());
//            }
//
//            return connection;
//
//        } catch (NoSuchElementException exception) {
//
//            if(channelPool.getNumIdle() == 0) {
//
//                pivot.getLogger().warning("Channel pool is full, adding object...");
//
//                channelPool.addObject();
//                return getPubSubConnection();
//            }
//
//            pivot.getLogger().log(Level.SEVERE,"Error while getting PubSubConnection" ,exception);
//            return null;
//
//        } catch (Exception exception) {
//            pivot.getLogger().log(Level.SEVERE,"Error while getting PubSubConnection" ,exception);
//            return null;
//        }
//    }


    @Override
    public RedisFuture<Void> subscribeAsync(String channel) {
        try {


            LettuceMessageListener lettuceMessageListener = new LettuceMessageListener(this,subscribeConnection);
            listeners.add(lettuceMessageListener);

            subscribeConnection.addListener(lettuceMessageListener);

            return subscribeConnection.async().subscribe(channel);

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while publishing message",exception);
        }
        return null;
    }

    @Override
    public RedisCache cache() {
        return cache;
    }

    @Override
    public void close() {

//        for (LettuceMessageListener listener : listeners) {
//            listener.getConnection().close();
//        }

        //channelPool.close();
        subscribeConnection.close();
        publishConnection.close();
        cachePool.close();
        client.shutdown();
    }
}
