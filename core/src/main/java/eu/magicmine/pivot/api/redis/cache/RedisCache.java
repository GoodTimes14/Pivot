package eu.magicmine.pivot.api.redis.cache;

import eu.magicmine.pivot.api.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class RedisCache {

    private final RedisManager manager;


    public String fetch(String key) {
        try(Jedis jedis = manager.getPool().getResource()) {
            if(!jedis.exists(key)) {
                return "";
            }
            return jedis.get(key);
        }
    }

    public void delete(String... keys) {
        try(Jedis jedis = manager.getPool().getResource()) {
            jedis.del(keys);
        }
    }


    public void insertMap(String key, Map<String,String> map) {
        try(Jedis jedis = manager.getPool().getResource()) {
            jedis.hset(key,map);
        }
    }

    public Map<String,String> getMap(String key) {
        try(Jedis jedis = manager.getPool().getResource()) {
            return jedis.hgetAll(key);
        }
    }

    public Set<String> keys(String pattern) {
        try(Jedis jedis = manager.getPool().getResource()) {
            return jedis.keys(pattern);
        }
    }

    public boolean exists(String key) {
        try(Jedis jedis = manager.getPool().getResource()) {
            return jedis.exists(key);
        }
    }

    public void initExpire(String key,int seconds) {
        try(Jedis jedis = manager.getPool().getResource()) {
            jedis.expire(key,seconds);
        }
    }

    public void persist(String key) {
        try(Jedis jedis = manager.getPool().getResource()) {
            jedis.persist(key);
        }
    }

    public void set(String key,String str) {
        try(Jedis jedis = manager.getPool().getResource()) {
            jedis.set(key,str);
        }
    }

}
