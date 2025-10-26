package com.example.service_one.service;

/**
 * Интерфейс сервиса для работы с Redis
 */
public interface RedisService {
    void saveData(byte[] data);
    byte[] getData();
    void saveSignature(byte[] signature);
    byte[] getSignature();
    void cleanup();
    boolean isRedisConnected();
}