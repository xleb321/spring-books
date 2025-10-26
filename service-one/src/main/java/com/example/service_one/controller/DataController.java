package com.example.service_one.controller;

import com.example.service_one.model.ApiResponse;
import com.example.service_one.service.RedisService;
import com.example.service_one.service.SignatureService;
import com.example.service_one.service.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Контроллер для обработки данных и подписи
 */
@RestController
@RequestMapping("/api/v1")
public class DataController {

    private static final Logger log = LoggerFactory.getLogger(DataController.class);
    private final RedisService redisService;
    private final SignatureService signatureService;
    private final WebClientService webClientService;

    public DataController(RedisService redisService, SignatureService signatureService, WebClientService webClientService) {
        this.redisService = redisService;
        this.signatureService = signatureService;
        this.webClientService = webClientService;
    }

    /**
     * Основной endpoint для обработки данных и асинхронной подписи
     */
    @PostMapping("/process-and-sign")
    public CompletableFuture<ResponseEntity<ApiResponse>> processAndSignData(
            @RequestParam(defaultValue = "204800") int dataSize) {

        log.info("Starting data processing and signing flow for size: {} bytes", dataSize);

        return CompletableFuture.supplyAsync(() -> {
                    // Этап 1: Генерация и сохранение данных
                    log.debug("Step 1: Generating random data");
                    byte[] randomData = generateRandomData(dataSize);

                    log.debug("Step 2: Saving data to Redis");
                    redisService.saveData(randomData);

                    log.info("Data generation completed, {} bytes saved to Redis", dataSize);
                    return "data-saved";
                })
                .thenCompose(result -> {
                    // Этап 2: Запуск процесса подписи в service-two
                    log.debug("Step 3: Triggering signature process in service-two");
                    return webClientService.triggerSignature();
                })
                .thenCompose(signatureStarted -> {
                    // Этап 3: Проверка успешного запуска подписи
                    if (!signatureStarted) {
                        log.error("Failed to start signature process in service-two");
                        throw new RuntimeException("Signature process initiation failed");
                    }

                    log.debug("Step 4: Waiting for signature completion...");
                    // Этап 4: Ожидание завершения подписи
                    return signatureService.waitForSignature("process-" + System.currentTimeMillis(),
                            Duration.ofMinutes(2));
                })
                .thenApply(signature -> {
                    // Этап 5: Формирование успешного ответа
                    log.info("Step 5: Process completed successfully, signature received: {} bytes",
                            signature.length);

                    Map<String, Object> responseData = Map.of(
                            "dataSize", dataSize,
                            "signatureLength", signature.length,
                            "status", "completed"
                    );
                    ApiResponse response = new ApiResponse(true, "Data processed and signed successfully", responseData);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    // Обработка ошибок
                    log.error("Process failed: {}", throwable.getMessage(), throwable);
                    Map<String, Object> errorData = Map.of("status", "error");
                    ApiResponse response = new ApiResponse(false, "Process failed: " + throwable.getMessage(), errorData);
                    return ResponseEntity.badRequest().body(response);
                });
    }

    private byte[] generateRandomData(int size) {
        log.debug("Generating secure random data of size: {} bytes", size);
        byte[] randomData = new byte[size];
        new SecureRandom().nextBytes(randomData);
        return randomData;
    }
}