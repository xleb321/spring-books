package com.example.service_one.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private static final String DATA_KEY = "service:random:data";
    private static final String SIGNATURE_KEY = "service:random:signature";

    @Autowired //повторяться не буду и далее тоже
    private RedisTemplate<String, byte[]> redisTemplate;

    public void saveData(byte[] data) {
        try {
            System.out.println("Saving data to Redis, size: " + data.length + " bytes"); //подключаете @Sl4j и пользуетесь log.info()
            redisTemplate.opsForValue().set(DATA_KEY, data);
            redisTemplate.expire(DATA_KEY, 5, TimeUnit.MINUTES); //с редисом не знаком, комментариев не оставлю
            System.out.println("Data saved successfully to Redis");
        } catch (Exception e) {
            System.err.println("Error saving data to Redis: " + e.getMessage());
            throw e;
        }
    }

    //методы где-то подсвечиваются? ide пишет no usages
    public byte[] getData() {
        try {
            byte[] data = redisTemplate.opsForValue().get(DATA_KEY);
            System.out.println("Retrieved data from Redis, size: " + (data != null ? data.length : "null") + " bytes");
            return data;
        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());
            throw e;
        }
    }

    public void saveSignature(byte[] signature) {
        try {
            System.out.println("Saving signature to Redis, size: " + signature.length + " bytes");
            redisTemplate.opsForValue().set(SIGNATURE_KEY, signature);
            redisTemplate.expire(SIGNATURE_KEY, 5, TimeUnit.MINUTES);
            System.out.println("Signature saved successfully to Redis");
        } catch (Exception e) {
            System.err.println("Error saving signature to Redis: " + e.getMessage());
            throw e;
        }
    }

    public byte[] getSignature() {
        try {
            byte[] signature = redisTemplate.opsForValue().get(SIGNATURE_KEY);
            System.out.println("Retrieved signature from Redis, size: " + (signature != null ? signature.length : "null") + " bytes");
            return signature;
        } catch (Exception e) {
            System.err.println("Error retrieving signature from Redis: " + e.getMessage());
            throw e;
        }
    }

    public void cleanup() {
        try {
            redisTemplate.delete(DATA_KEY);
            redisTemplate.delete(SIGNATURE_KEY);
            System.out.println("Redis cleanup completed");
        } catch (Exception e) {
            System.err.println("Error during Redis cleanup: " + e.getMessage());
            throw e;
        }
    }

    // Добавим метод для проверки подключения
    public boolean isRedisConnected() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            System.err.println("Redis connection test failed: " + e.getMessage());
            return false;
        }
    }
}