package eu.magicmine.pivot.api.redis;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.redis.cache.RedisCache;
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
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

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
    private GenericObjectPool<StatefulRedisPubSubConnection<String, String>> pool;
    private List<LettuceMessageListener> listeners = new ArrayList<>();

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
                .ioThreadPoolSize(4)
                .computationThreadPoolSize(4)
                .build();

        client = RedisClient.create(res,uri);

        client.setOptions(
                ClientOptions.builder()
                        .autoReconnect(true)
                        .protocolVersion(ProtocolVersion.RESP3).build());

        pool = ConnectionPoolSupport
                .createGenericObjectPool(client::connectPubSub, new GenericObjectPoolConfig<>());

    }

    @Override
    public long publish(String channel, String message) {
        try(StatefulRedisConnection<String, String> connection = pool.borrowObject()) {

            return connection.sync().publish(channel, message);

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while publishing message",exception);
        }
        return -1;
    }

    @Override
    public RedisFuture<Long> publishAsync(String channel, String message) {
        try(StatefulRedisConnection<String, String> connection = pool.borrowObject()) {

            return connection.async().publish(channel, message);

        } catch (Exception exception) {
            pivot.getLogger().log(Level.SEVERE,"Error while publishing message",exception);
        }

        return null;
    }


    @Override
    public void subscribe(String channel) {
        try {

            StatefulRedisPubSubConnection<String,String> connection = getConnection();


            LettuceMessageListener lettuceMessageListener = new LettuceMessageListener(this,connection);
            listeners.add(lettuceMessageListener);

            connection.sync().subscribe(channel);
            connection.addListener(lettuceMessageListener);

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

    public StatefulRedisPubSubConnection<String,String> getConnection() throws Exception {

        StatefulRedisPubSubConnection<String,String> connection = pool.borrowObject();

        if(connectionData.isAuth()) {
            connection.sync().auth(connectionData.getPassword());
        }

        return connection;
    }


    @Override
    public RedisFuture<Void> subscribeAsync(String channel) {
        try {

            StatefulRedisPubSubConnection<String, String> connection = getConnection();

            LettuceMessageListener lettuceMessageListener = new LettuceMessageListener(this,connection);
            listeners.add(lettuceMessageListener);

            connection.addListener(lettuceMessageListener);

            return connection.async().subscribe(channel);

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

        for (LettuceMessageListener listener : listeners) {
            listener.getConnection().close();
        }

        pool.close();
        client.shutdown();
    }
}
