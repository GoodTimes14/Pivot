package eu.magicmine.pivot.api.utils.redis;

import lombok.Data;

@Data
public class RedisMessage {

    private final String channel,message;

}
