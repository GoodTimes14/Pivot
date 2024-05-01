package eu.magicmine.pivot.api.utils.redis;

import eu.magicmine.pivot.api.redis.listener.RedisListener;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class RedisMethod {

    private final RedisListener holder;
    private final RedisListen annotation;
    private final Method method;
}
