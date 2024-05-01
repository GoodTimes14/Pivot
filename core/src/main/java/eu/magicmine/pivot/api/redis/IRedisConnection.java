package eu.magicmine.pivot.api.redis;

import eu.magicmine.pivot.api.redis.cache.RedisCache;
import eu.magicmine.pivot.api.redis.listener.RedisListener;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import io.lettuce.core.RedisFuture;

public interface IRedisConnection {

    void connect(ConnectionData data);

    long publish(String channel,String message);

    RedisFuture<Long> publishAsync(String channel, String message);
    void subscribe(String channel);

    void hopperMessage(String channel,String message);

    void registerListener(RedisListener listener);

    default void registerListeners(RedisListener... listeners) {
        for (RedisListener listener : listeners) {
            registerListener(listener);
        }
    }

    RedisFuture<Void> subscribeAsync(String channel);

    RedisCache cache();

    void close();

}
