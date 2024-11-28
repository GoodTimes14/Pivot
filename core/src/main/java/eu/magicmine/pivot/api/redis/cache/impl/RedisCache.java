package eu.magicmine.pivot.api.redis.cache.impl;

import eu.magicmine.pivot.api.redis.LettuceConnection;
import eu.magicmine.pivot.api.redis.cache.IRedisCache;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@RequiredArgsConstructor
public class RedisCache implements IRedisCache {

    private final LettuceConnection lettuceConnection;


    @Override
    public String fetch(String key) {

        if(exists(key)) {

            try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

                return connection.sync().get(key);
            } catch (Exception e) {

                lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while fetching key",e);
            }
        }

        return "";
    }

    @Override
    public void delete(String... keys) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().del(keys);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }


    @Override
    public void insertMap(String key, Map<String, String> map) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().hmset(key,map);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while inserting Map",e);
        }

    }

    @Override
    public void updateMap(String key, String field, String value) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().hset(key,field,value);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while updating Map",e);
        }

    }

    @Override
    public Map<String,String> getMap(String key) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            return connection.sync().hgetall(key);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while getting Map",e);
        }
        return null;
    }

    @Override
    public List<String> keys(String pattern) {


        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            return connection.sync().keys(pattern);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
        return null;
    }

    @Override
    public boolean exists(String key) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            return connection.sync().exists(key) == 1;

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }

        return false;
    }

    @Override
    public void initExpire(String key, int seconds) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().expire(key,seconds);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

    @Override
    public void persist(String key) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().persist(key);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

    @Override
    public void set(String key, String str) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getCacheConnection()) {

            connection.sync().set(key,str);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

}
