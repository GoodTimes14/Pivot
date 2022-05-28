package eu.magicmine.pivot.api.redis;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.redis.cache.RedisCache;
import eu.magicmine.pivot.api.redis.pub.PublisherThread;
import eu.magicmine.pivot.api.redis.sub.listener.RedisListener;
import eu.magicmine.pivot.api.redis.sub.thread.SubscriberThread;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import eu.magicmine.pivot.api.utils.redis.RedisListen;
import eu.magicmine.pivot.api.utils.redis.RedisMessage;
import eu.magicmine.pivot.api.utils.redis.RedisMethod;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

@Getter
public class RedisManager {

    private final Pivot pivot;
    private final ConnectionData data;
    private final RedisCache cache;
    private final Map<String, SubscriberThread> subscribers;
    private final Map<String, List<RedisMethod>> methodMap;
    private JedisPool pool;
    private PublisherThread publisherThread;


    public RedisManager(Pivot pivot, ConnectionData data) {
        this.pivot = pivot;
        this.data = data;
        subscribers = new HashMap<>();
        methodMap = new HashMap<>();
        openConnection();
        cache = new RedisCache(this);
    }

    public void openConnection() {
        pool =  data.isAuth() ? new JedisPool(new JedisPoolConfig(), data.getHost(), data.getPort(), Protocol.DEFAULT_TIMEOUT,data.getPassword()) :
                new JedisPool(new JedisPoolConfig(), data.getHost(), data.getPort(), Protocol.DEFAULT_TIMEOUT);
        publisherThread = new PublisherThread(this);
        publisherThread.start();
    }

    public Jedis newJedis() {
        Jedis jedis = new Jedis(data.getHost(),data.getPort());
        if(data.isAuth()) {
            jedis.auth(data.getPassword());
        }
        return jedis;
    }


    public void loadListener(RedisListener listener) {
        for(Method method : listener.getClass().getMethods()) {
            if(method.isAnnotationPresent(RedisListen.class)) {
                //System.out.println(method.getName());
                RedisListen annotation = method.getAnnotation(RedisListen.class);
                if(!subscribers.containsKey(annotation.channel())) {
                    subscribe(annotation.channel());
                }
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

    public void loadListeners(RedisListener... listeners) {
        for(RedisListener listener : listeners) {
            loadListener(listener);
        }
    }

    private void subscribe(String channel) {
        SubscriberThread subscriberThread = new SubscriberThread(channel,this);
        subscriberThread.start();
        subscribers.put(channel,subscriberThread);
    }

    public void hopperMessage(String channel,String message) {
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

    public void publish(String channel,String message) {
        getPublisherThread().add(new RedisMessage(channel,message));
    }

}
