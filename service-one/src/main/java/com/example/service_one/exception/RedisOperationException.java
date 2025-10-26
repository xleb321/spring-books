package com.example.service_one.exception;

/**
 * Исключение для ошибок операций с Redis
 */
public class RedisOperationException extends RuntimeException {
    public RedisOperationException(String message) {
        super(message);
    }
    public RedisOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}