package com.minidouban.component;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Random;

@Component
public class JedisUtils {
    @Resource
    private JedisPool jedisPool;
    private static final int floatExpiredSeconds = 60 * 15;

    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public String setExpire(String key, int seconds, String value) {
        Random random = new Random();
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds + random.nextInt(floatExpiredSeconds), value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }
}
