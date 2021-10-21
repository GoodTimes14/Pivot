package eu.magicmine.pivot.api.redis.pub;


import eu.magicmine.pivot.api.redis.RedisManager;
import eu.magicmine.pivot.api.utils.redis.RedisMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor
public class PublisherThread extends Thread {

    private final RedisManager manager;
    private final BlockingQueue<RedisMessage> messageQueue = new LinkedBlockingQueue<>();

    @SneakyThrows
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            RedisMessage message = messageQueue.take();
            try(Jedis jedis = manager.getPool().getResource()) {
                jedis.publish(message.getChannel(), message.getMessage());
            }
        }
    }

    @SneakyThrows
    public void add(RedisMessage message) {
        messageQueue.put(message);
    }
}
