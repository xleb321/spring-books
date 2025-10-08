package com.example.service_two.controller;

import com.example.service_two.service.RedisService;
import com.example.service_two.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignatureController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SignatureService signatureService;

    @GetMapping("/sign")
    public ResponseEntity<ApiResponse> signData() {
        ApiResponse response = new ApiResponse();

        try {
            if (!redisService.isDataAvailable()) {
                response.setSuccess(false);
                response.setMessage("No data on Redis.");
                return ResponseEntity.badRequest().body(response);
            }

            byte[] data = redisService.getData();
            System.out.println("Data.length: " + data.length + " (b)");

            byte[] signature = signatureService.signData(data);
            System.out.println("Signature.length: " + signature.length + " (b)");

            redisService.saveSignature(signature);

            response.setSuccess(true);
            response.setMessage("Signature saved to Redis");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error in creation creation: " + e.getMessage());
            e.fillInStackTrace();
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
