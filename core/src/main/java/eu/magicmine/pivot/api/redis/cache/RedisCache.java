package eu.magicmine.pivot.api.redis.cache;

import eu.magicmine.pivot.api.redis.LettuceConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@RequiredArgsConstructor
public class RedisCache {

    private final LettuceConnection lettuceConnection;


    public String fetch(String key) {

        if(exists(key)) {

            try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

                return connection.sync().get(key);
            } catch (Exception e) {

                lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while fetching key",e);
            }
        }

        return "";
    }

    public void delete(String... keys) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            connection.sync().del(keys);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }


    public void insertMap(String key, Map<String,String> map) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            connection.sync().hset(key,map);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }

    }

    public Map<String,String> getMap(String key) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            return connection.sync().hgetall(key);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
        return null;
    }

    public List<String> keys(String pattern) {


        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            return connection.sync().keys(pattern);
        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
        return null;
    }

    public boolean exists(String key) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            return connection.sync().exists(key) == 1;

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }

        return false;
    }

    public void initExpire(String key,int seconds) {

        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            connection.sync().expire(key,seconds);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

    public void persist(String key) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            connection.sync().persist(key);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

    public void set(String key,String str) {
        try(StatefulRedisConnection<String,String> connection = lettuceConnection.getConnection()) {

            connection.sync().set(key,str);

        } catch (Exception e) {

            lettuceConnection.getPivot().getLogger().log(Level.SEVERE,"Error while deleting key",e);
        }
    }

}
