package com.ssport.repository.redis.impl;

import com.ssport.repository.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void putObject(String key, String value, Date expiration) {
        redisTemplate.boundValueOps(key).set(value);
        long expTimeMillis = expiration.getTime() - System.currentTimeMillis();
        redisTemplate.expire(key, expTimeMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getObject(String key) {
        return redisTemplate.boundValueOps(key).get();
    }
}
