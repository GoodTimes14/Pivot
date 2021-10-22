package eu.magicmine.pivot.api.redis.sub.thread;

import eu.magicmine.pivot.api.redis.RedisManager;
import eu.magicmine.pivot.api.redis.sub.RedisSubscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.logging.Level;

public class SubscriberThread extends Thread {

    private String channel;
    private RedisSubscriber subscriber;
    private final RedisManager manager;
    private boolean lol;



    public SubscriberThread(String channel, RedisManager manager) {
        this.manager = manager;
        this.channel = channel;
        subscriber = new RedisSubscriber(manager);
    }



    @Override
    public void run() {
        while (!interrupted() && !manager.getPool().isClosed()) {
            try(Jedis jedis = manager.newJedis()) {
                if(lol) {
                    manager.getPivot().getLogger().log(Level.FINE,"Redis connection re-established");
                }
                jedis.subscribe(subscriber,channel);
            } catch (Exception ex) {
                manager.getPivot().getLogger().log(Level.WARNING,"Redis connection has dropped,trying to reconnect...");
                ex.printStackTrace();
                lol = true;
                try {
                    subscriber.unsubscribe();
                } catch (JedisConnectionException exception) {

                }
                try {
                    sleep(3000);
                } catch (InterruptedException ignored) {
                    interrupt();
                }
            }
        }
    }
}
