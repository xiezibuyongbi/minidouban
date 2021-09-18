package com.minidouban.component;

import org.springframework.stereotype.Component;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Component
public class CacheKeyGenerator {

    public String getRedisKey(String redisKeyPrefix, long entryId) {
        return redisKeyPrefix + md5DigestAsHex(String.valueOf(entryId).getBytes());
    }

    public String getRedisKey(String redisKeyPrefix, String entryId) {
        return redisKeyPrefix + md5DigestAsHex(entryId.getBytes());
    }
}
