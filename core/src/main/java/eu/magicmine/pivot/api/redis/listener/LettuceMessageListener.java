package eu.magicmine.pivot.api.redis.listener;

import eu.magicmine.pivot.api.redis.LettuceConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LettuceMessageListener implements RedisPubSubListener<String , String> {

    private final LettuceConnection lettuce;
    @Getter
    private final StatefulRedisPubSubConnection<String,String> connection;

    @Override
    public void message(String channel, String message) {
        lettuce.hopperMessage(channel, message);
    }

    @Override
    public void message(String s, String k1, String s2) {}

    @Override
    public void subscribed(String channel, long count) {
        lettuce.getPivot().getLogger().fine("Subscribed to channel: " + channel + " (" + count + ")");
    }

    @Override
    public void psubscribed(String pattern, long count) {}

    @Override
    public void unsubscribed(String channel, long count) {}

    @Override
    public void punsubscribed(String pattern, long count) {}
}

