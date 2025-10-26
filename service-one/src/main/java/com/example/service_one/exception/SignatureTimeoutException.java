package com.example.service_one.exception;

/**
 * Исключение для таймаута ожидания подписи
 */
public class SignatureTimeoutException extends RuntimeException {
    public SignatureTimeoutException(String message) {
        super(message);
    }
    public SignatureTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}