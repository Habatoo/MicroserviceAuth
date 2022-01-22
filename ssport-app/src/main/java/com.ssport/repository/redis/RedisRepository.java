package com.ssport.repository.redis;

import java.util.Date;

public interface RedisRepository {
    void putObject(String key, String value, Date expiration);

    String getObject(String key);
}
