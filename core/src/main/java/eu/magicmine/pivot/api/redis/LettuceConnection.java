package eu.magicmine.pivot.api.redis;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.redis.cache.RedisCache;
import eu.magicmine.pivot.api.redis.listener.RedisListener;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import eu.magicmine.pivot.api.utils.redis.RedisListen;
import eu.magicmine.pivot.api.utils.redis.RedisMethod;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

@Getter
public class LettuceConnection implements IRedisConnection {

    private final Pivot pivot;
    private final RedisCache cache;
    private final Map<String, List<RedisMethod>> methodMap;
    private RedisClient client;
    private StatefulRedisPubSubConnection<String,String> connection;


    public LettuceConnection(Pivot pivot, ConnectionData data) {
        this.pivot = pivot;
        methodMap = new HashMap<>();

        cache = new RedisCache(this);
        connect(data);
    }

    @Override
    public void connect(ConnectionData data) {
        RedisURI uri = RedisURI.create(data.getHost(),data.getPort());
        client = RedisClient.create(uri);

        connection = client.connectPubSub();

        if (data.isAuth()) {
            String response = connection.sync().auth(data.getPassword());
            pivot.getLogger().info("Auth response from redis server:" + response);
        }

    }

    @Override
    public long publish(String channel, String message) {
        return connection.sync().publish(channel, message);
    }

    @Override
    public RedisFuture<Long> publishAsync(String channel, String message) {
        return connection.async().publish(channel,message);
    }

    @Override
    public void subscribe(String channel) {
        connection.sync().subscribe(channel);
    }

    @Override
    public void hopperMessage(String channel, String message) {
        //System.out.println(channel);
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
                //System.out.println(method.getName());
                RedisListen annotation = method.getAnnotation(RedisListen.class);
                if(method.getParameterTypes().length != 1 ) {
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


    @Override
    public RedisFuture<Void> subscribeAsync(String channel) {
        return connection.async().subscribe(channel);
    }

    @Override
    public RedisCache cache() {
        return cache;
    }

    @Override
    public void close() {
        connection.close();
    }
}
