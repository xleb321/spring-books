package com.example.service_two.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private static final String DATA_KEY = "service:random:data";
    private static final String SIGNATURE_KEY = "service:random:signature";

    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;

    public byte[] getData() {
        return redisTemplate.opsForValue().get(DATA_KEY);
    }

    public void saveSignature(byte[] signature) {
        redisTemplate.opsForValue().set(SIGNATURE_KEY, signature);
        redisTemplate.expire(SIGNATURE_KEY, 5, TimeUnit.MINUTES);
    }

    public boolean isDataAvailable() {
        return redisTemplate.hasKey(DATA_KEY);
    }

}
