package com.example.service_one.exception;

/**
 * Исключение для ошибок обработки данных
 */
public class DataProcessingException extends RuntimeException {
    public DataProcessingException(String message) {
        super(message);
    }
    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}