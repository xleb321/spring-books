package com.example.service_two.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Реализация сервиса Redis для Service Two
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);
    private static final String DATA_KEY = "service:random:data";
    private static final String SIGNATURE_KEY = "service:random:signature";
    private static final String PROCESSED_KEY_PREFIX = "service:processed:";
    private static final long REDIS_TTL_MINUTES = 5;

    private final RedisTemplate<String, byte[]> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public byte[] getData() {
        try {
            byte[] data = redisTemplate.opsForValue().get(DATA_KEY);
            if (data != null) {
                log.debug("Retrieved data from Redis for signing, size: {} bytes", data.length);
            }
            return data;
        } catch (Exception e) {
            log.error("Failed to retrieve data from Redis: {}", e.getMessage(), e);
            throw new RuntimeException("Redis data retrieval failed", e);
        }
    }

    @Override
    public void saveSignature(byte[] signature) {
        try {
            log.debug("Saving signature to Redis, size: {} bytes", signature.length);
            redisTemplate.opsForValue().set(SIGNATURE_KEY, signature);
            redisTemplate.expire(SIGNATURE_KEY, REDIS_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("Signature saved to Redis successfully");
        } catch (Exception e) {
            log.error("Failed to save signature to Redis: {}", e.getMessage(), e);
            throw new RuntimeException("Redis signature save failed", e);
        }
    }

    @Override
    public boolean isDataAvailable() {
        try {
            Boolean hasKey = redisTemplate.hasKey(DATA_KEY);
            log.debug("Data availability check: {}", hasKey);
            return hasKey != null && hasKey;
        } catch (Exception e) {
            log.error("Failed to check data availability in Redis: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void markDataAsProcessed(String dataKey) {
        try {
            String processedKey = PROCESSED_KEY_PREFIX + dataKey;
            redisTemplate.opsForValue().set(processedKey, "processed".getBytes());
            redisTemplate.expire(processedKey, REDIS_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Marked data as processed: {}", dataKey);
        } catch (Exception e) {
            log.error("Failed to mark data as processed: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isDataProcessed(String dataKey) {
        try {
            String processedKey = PROCESSED_KEY_PREFIX + dataKey;
            Boolean exists = redisTemplate.hasKey(processedKey);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Failed to check if data is processed: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getCurrentDataKey() {
        return DATA_KEY;
    }
}