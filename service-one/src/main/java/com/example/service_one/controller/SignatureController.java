package com.example.service_one.controller;

import com.example.service_one.model.ApiResponse;
import com.example.service_one.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для операций с подписями
 */
@RestController
@RequestMapping("/api/v1")
public class SignatureController {

    private static final Logger log = LoggerFactory.getLogger(SignatureController.class);
    private final RedisService redisService;

    public SignatureController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/signature/status")
    public ResponseEntity<ApiResponse> getSignatureStatus() {
        log.debug("Checking signature status");

        try {
            byte[] signature = redisService.getSignature();
            boolean signatureAvailable = signature != null && signature.length > 0;
            boolean dataAvailable = redisService.getData() != null;

            Map<String, Object> statusData = Map.of(
                    "signatureAvailable", signatureAvailable,
                    "signatureSize", signatureAvailable ? signature.length : 0,
                    "dataAvailable", dataAvailable,
                    "redisConnected", redisService.isRedisConnected()
            );

            ApiResponse response = new ApiResponse(true, "Signature status retrieved successfully", statusData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking signature status: {}", e.getMessage(), e);
            ApiResponse response = new ApiResponse(false, "Error checking signature status: " + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<ApiResponse> cleanup() {
        log.info("Manual cleanup requested");

        try {
            redisService.cleanup();
            ApiResponse response = new ApiResponse(true, "Redis data cleaned up successfully", null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during cleanup: {}", e.getMessage(), e);
            ApiResponse response = new ApiResponse(false, "Cleanup failed: " + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}