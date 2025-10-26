package com.example.service_one.service;

import com.example.service_one.exception.RedisOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Реализация сервиса для работы с Redis
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);
    private static final String DATA_KEY = "service:random:data";
    private static final String SIGNATURE_KEY = "service:random:signature";
    private static final long REDIS_TTL_MINUTES = 5;

    private final RedisTemplate<String, byte[]> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveData(byte[] data) {
        try {
            log.debug("Saving data to Redis with key: {}, size: {} bytes", DATA_KEY, data.length);
            redisTemplate.opsForValue().set(DATA_KEY, data);
            redisTemplate.expire(DATA_KEY, REDIS_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("Data successfully saved to Redis, TTL: {} minutes", REDIS_TTL_MINUTES);
        } catch (Exception e) {
            log.error("Redis save operation failed for key: {}", DATA_KEY, e);
            throw new RedisOperationException("Failed to save data to Redis", e);
        }
    }

    @Override
    public byte[] getData() {
        try {
            byte[] data = redisTemplate.opsForValue().get(DATA_KEY);
            log.debug("Retrieved data from Redis, size: {} bytes",
                    data != null ? data.length : 0);
            return data;
        } catch (Exception e) {
            log.error("Redis get operation failed for key: {}", DATA_KEY, e);
            throw new RedisOperationException("Failed to retrieve data from Redis", e);
        }
    }

    @Override
    public byte[] getSignature() {
        try {
            byte[] signature = redisTemplate.opsForValue().get(SIGNATURE_KEY);
            if (signature != null) {
                log.debug("Signature found in Redis, size: {} bytes", signature.length);
            }
            return signature;
        } catch (Exception e) {
            log.error("Redis get operation failed for key: {}", SIGNATURE_KEY, e);
            throw new RedisOperationException("Failed to retrieve signature from Redis", e);
        }
    }

    @Override
    public void saveSignature(byte[] signature) {
        try {
            log.debug("Saving signature to Redis with key: {}, size: {} bytes",
                    SIGNATURE_KEY, signature.length);
            redisTemplate.opsForValue().set(SIGNATURE_KEY, signature);
            redisTemplate.expire(SIGNATURE_KEY, REDIS_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("Signature successfully saved to Redis, TTL: {} minutes", REDIS_TTL_MINUTES);
        } catch (Exception e) {
            log.error("Redis save operation failed for key: {}", SIGNATURE_KEY, e);
            throw new RedisOperationException("Failed to save signature to Redis", e);
        }
    }

    @Override
    public void cleanup() {
        try {
            Boolean dataDeleted = redisTemplate.delete(DATA_KEY);
            Boolean signatureDeleted = redisTemplate.delete(SIGNATURE_KEY);
            log.info("Redis cleanup completed - data deleted: {}, signature deleted: {}",
                    dataDeleted, signatureDeleted);
        } catch (Exception e) {
            log.error("Redis cleanup operation failed", e);
            throw new RedisOperationException("Failed to cleanup Redis", e);
        }
    }

    @Override
    public boolean isRedisConnected() {
        try {
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            boolean connected = result != null && result.equalsIgnoreCase("PONG");
            log.debug("Redis connection check: {}", connected ? "connected" : "disconnected");
            return connected;
        } catch (Exception e) {
            log.warn("Redis connection test failed: {}", e.getMessage());
            return false;
        }
    }
}