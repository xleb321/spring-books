package com.example.service_two.exception;

/**
 * Исключение для ошибок создания подписи
 */
public class SignatureException extends RuntimeException {
    public SignatureException(String message) {
        super(message);
    }
    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}