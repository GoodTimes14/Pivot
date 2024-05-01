package eu.magicmine.pivot.api.redis.cache;

import eu.magicmine.pivot.api.redis.LettuceConnection;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RedisCache {

    private final LettuceConnection lettuceConnection;


    public String fetch(String key) {

        if(exists(key)) {
            return lettuceConnection.getConnection().sync().get(key);
        }

        return "";
    }

    public void delete(String... keys) {
        lettuceConnection.getConnection().sync().del(keys);
    }


    public void insertMap(String key, Map<String,String> map) {
        lettuceConnection.getConnection().sync().hset(key,map);
    }

    public Map<String,String> getMap(String key) {
       return lettuceConnection.getConnection().sync().hgetall(key);
    }

    public List<String> keys(String pattern) {
        return lettuceConnection.getConnection().sync().keys(pattern);
    }

    public boolean exists(String key) {
       return lettuceConnection.getConnection().sync().exists(key) == 1;
    }

    public void initExpire(String key,int seconds) {
        lettuceConnection.getConnection().sync().expire(key,seconds);
    }

    public void persist(String key) {
        lettuceConnection.getConnection().sync().persist(key);
    }

    public void set(String key,String str) {
        lettuceConnection.getConnection().sync().set(key,str);
    }

}
