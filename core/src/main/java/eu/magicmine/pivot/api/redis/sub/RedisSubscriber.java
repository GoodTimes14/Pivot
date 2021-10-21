package eu.magicmine.pivot.api.redis.sub;

import eu.magicmine.pivot.api.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

@RequiredArgsConstructor
public class RedisSubscriber extends JedisPubSub {

    private final RedisManager manager;

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        manager.getPivot().getLogger().log(Level.FINE,"[Redis] Successfully subscribed to channel: " + channel);
    }

    @Override
    public void onMessage(String channel, String message) {
        manager.hopperMessage(channel,message);
    }
}
