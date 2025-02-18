package eu.magicmine.pivot.api.redis.cache.impl;

import eu.magicmine.pivot.api.redis.LettuceConnection;
import eu.magicmine.pivot.api.redis.cache.IRedisCache;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RedisCache implements IRedisCache {

    private final LettuceConnection lettuceConnection;


    @Override
    public String fetch(String key) {

        if(exists(key)) {
            return lettuceConnection.getInteractionConnection().sync().get(key);
        }

        return "";
    }

    @Override
    public void delete(String... keys) {
        lettuceConnection.getInteractionConnection().sync().del(keys);
    }


    @Override
    public void insertMap(String key, Map<String, String> map) {
        lettuceConnection.getInteractionConnection().async().hmset(key,map);
    }

    @Override
    public void updateMap(String key, String field, String value) {
        lettuceConnection.getInteractionConnection().async().hset(key,field,value);
    }

    @Override
    public Map<String,String> getMap(String key) {
        return lettuceConnection.getInteractionConnection().sync().hgetall(key);
    }

    @Override
    public List<String> keys(String pattern) {
        return lettuceConnection.getInteractionConnection().sync().keys(pattern);
    }

    @Override
    public boolean exists(String key) {
        return lettuceConnection.getInteractionConnection().sync().exists(key) == 1;
    }

    @Override
    public void initExpire(String key, int seconds) {
        lettuceConnection.getInteractionConnection().async().expire(key,seconds);
    }

    @Override
    public void persist(String key) {
        lettuceConnection.getInteractionConnection().async().persist(key);
    }

    @Override
    public void set(String key, String str) {
        lettuceConnection.getInteractionConnection().async().set(key,str);
    }

}
