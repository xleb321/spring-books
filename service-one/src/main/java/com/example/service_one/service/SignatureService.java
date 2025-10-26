package com.example.service_one.service;

import com.example.service_one.exception.SignatureTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для управления процессом ожидания подписи
 */
@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);
    private final RedisService redisService;

    public SignatureService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Async
    public CompletableFuture<byte[]> waitForSignature(String correlationId, Duration timeout) {
        return CompletableFuture.supplyAsync(() -> {
            Instant startTime = Instant.now();
            int pollCount = 0;

            log.info("[{}] Starting signature wait with timeout: {}", correlationId, timeout);

            while (Duration.between(startTime, Instant.now()).compareTo(timeout) < 0) {
                try {
                    pollCount++;
                    byte[] signature = redisService.getSignature();

                    if (signature != null && signature.length > 0) {
                        log.info("[{}] Signature received successfully after {} polls",
                                correlationId, pollCount);
                        return signature;
                    }

                    if (pollCount % 10 == 0) {
                        log.debug("[{}] Poll {}: signature not ready, waiting...",
                                correlationId, pollCount);
                    }

                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[{}] Signature waiting interrupted", correlationId);
                    throw new SignatureTimeoutException("Signature waiting interrupted for: " + correlationId);
                }
            }

            String errorMessage = String.format(
                    "Signature timeout after %s (polls: %d) for: %s",
                    timeout, pollCount, correlationId
            );
            log.error("[{}] {}", correlationId, errorMessage);
            throw new SignatureTimeoutException(errorMessage);
        });
    }
}