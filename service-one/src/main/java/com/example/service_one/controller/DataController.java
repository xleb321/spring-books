package com.example.service_one.controller;

import com.example.service_one.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
public class DataController {

    @Autowired
    private RedisService redisService;

    private static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEQuLkfUsgdE7V1gnp60feVXgahObQsegvqK8jFKxq7QLTjj3m4bvPKxJ3O3yhRrzZjqAlx9aDn/h5zf69gqOiDg==";

    @GetMapping("/process")
    public ResponseEntity<ApiResponse> processData () {
        ApiResponse response = new ApiResponse();

        try {
            int dataSize = 200 * 1024;
            byte[] randomData = new byte[dataSize];
            new SecureRandom().nextBytes(randomData);

            redisService.saveData(randomData);

            response.setMessage("Data saved!");
            response.setSuccess(true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Data error: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

}
