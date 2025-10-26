package com.example.service_two.controller;

import com.example.service_two.model.ApiResponse;
import com.example.service_two.service.AutoSignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для ручного запуска подписи (опционально)
 */
@RestController
@RequestMapping("/api/v1")
public class SignatureController {

    private static final Logger log = LoggerFactory.getLogger(SignatureController.class);
    private final AutoSignatureService autoSignatureService;

    public SignatureController(AutoSignatureService autoSignatureService) {
        this.autoSignatureService = autoSignatureService;
    }

    /**
     * Ручной запуск процесса подписи
     */
    @PostMapping("/sign")
    public ResponseEntity<ApiResponse> signData() {
        log.info("Manual signature process triggered");

        try {
            boolean result = autoSignatureService.processAvailableData();

            if (result) {
                ApiResponse response = new ApiResponse(true, "Signature created successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse response = new ApiResponse(false, "No data available for signing", null);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("Error in manual signature process: {}", e.getMessage(), e);
            ApiResponse response = new ApiResponse(false, "Signature creation error: " + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Получение статуса автоматической службы
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getStatus() {
        boolean isRunning = autoSignatureService.isAutoProcessingEnabled();
        int processedCount = autoSignatureService.getProcessedCount();

        ApiResponse response = new ApiResponse(true, "Service status retrieved",
                new StatusData(isRunning, processedCount));
        return ResponseEntity.ok(response);
    }

    /**
     * Включение/выключение автоматической обработки
     */
    @PostMapping("/auto-processing/{enabled}")
    public ResponseEntity<ApiResponse> setAutoProcessing(@PathVariable boolean enabled) {
        autoSignatureService.setAutoProcessingEnabled(enabled);

        ApiResponse response = new ApiResponse(true,
                "Auto processing " + (enabled ? "enabled" : "disabled"), null);
        return ResponseEntity.ok(response);
    }

    // Вспомогательный класс для данных статуса
    public static class StatusData {
        private final boolean autoProcessingEnabled;
        private final int processedCount;

        public StatusData(boolean autoProcessingEnabled, int processedCount) {
            this.autoProcessingEnabled = autoProcessingEnabled;
            this.processedCount = processedCount;
        }

        public boolean isAutoProcessingEnabled() {
            return autoProcessingEnabled;
        }

        public int getProcessedCount() {
            return processedCount;
        }
    }
}