package com.minidouban.cachemgr.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Component
public class JedisUtils {
    private static final int floatExpiredSeconds = 60 * 15;
    @Resource
    private JedisPool jedisPool;

    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public String setExpire(String key, int seconds, String value) {
        Random random = new Random();
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis
                    .setex(key, seconds + random.nextInt(floatExpiredSeconds),
                            value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }

    public Boolean delIfExisting(String key) {
        return (Boolean) eval(
                "local isExisting = redis.call('exists', keys[1]) " +
                        "if isExisting then " +
                        "local delResult = redis.call('del',keys[1]) " +
                        "if delResult == 1 then return true " +
                        "else return false end " +
                        "else return true end", List.of(key), List.of());
    }

    public long zAddExpire(String key, String member, double score) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, member);
        }
    }

    public long zScore(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return (long) jedis.zscore(key, member).doubleValue();
        }
    }

    public long zremRangeByScore(String key, long min, long max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, (double) min, (double) max);
        }
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.eval(script, keys, args);
        }
    }
}


