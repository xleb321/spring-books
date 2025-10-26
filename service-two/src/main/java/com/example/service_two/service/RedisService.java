package com.example.service_two.service;

/**
 * Интерфейс сервиса для работы с Redis
 */
public interface RedisService {
    byte[] getData();
    void saveSignature(byte[] signature);
    boolean isDataAvailable();
    void markDataAsProcessed(String dataKey);
    boolean isDataProcessed(String dataKey);
    String getCurrentDataKey();
}