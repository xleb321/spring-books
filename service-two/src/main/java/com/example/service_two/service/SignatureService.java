package com.example.service_two.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Сервис для создания цифровых подписей
 */
@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

    @Value("${app.private-key}")
    private String privateKeyBase64;

    /**
     * Создает цифровую подпись для данных
     */
    public byte[] signData(byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        log.info("Starting data signing process for {} bytes", data.length);

        // Декодирование приватного ключа
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

        // Создание объекта PrivateKey
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(
                new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes)
        );

        // Создание подписи
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] digitalSignature = signature.sign();

        log.info("Data signed successfully, signature size: {} bytes", digitalSignature.length);
        return digitalSignature;
    }

    /**
     * Проверяет валидность приватного ключа
     */
    public boolean isPrivateKeyValid() {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes));
            return true;
        } catch (Exception e) {
            log.error("Private key validation failed: {}", e.getMessage());
            return false;
        }
    }
}