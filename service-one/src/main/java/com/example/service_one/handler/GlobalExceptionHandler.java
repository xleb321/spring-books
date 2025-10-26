package com.example.service_one.handler;

import com.example.service_one.exception.RedisOperationException;
import com.example.service_one.exception.SignatureTimeoutException;
import com.example.service_one.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Глобальный обработчик исключений
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SignatureTimeoutException.class)
    public ResponseEntity<ApiResponse> handleSignatureTimeout(SignatureTimeoutException e) {
        log.warn("Signature timeout occurred: {}", e.getMessage());
        ApiResponse response = new ApiResponse(false, e.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ApiResponse> handleRedisOperationException(RedisOperationException e) {
        log.error("Redis operation failed: {}", e.getMessage(), e);
        ApiResponse response = new ApiResponse(false, "Storage operation failed: " + e.getMessage(), null);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse> handleWebClientException(WebClientResponseException e) {
        log.error("Service communication error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        ApiResponse response = new ApiResponse(false, "Service communication error: " + e.getStatusCode(), null);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        ApiResponse response = new ApiResponse(false, "Internal server error: " + e.getMessage(), null);
        return ResponseEntity.internalServerError().body(response);
    }
}