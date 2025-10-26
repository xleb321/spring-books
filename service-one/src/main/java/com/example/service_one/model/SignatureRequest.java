package com.example.service_one.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель запроса для операций с подписями
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureRequest {
    private String correlationId;
    private Integer dataSize;
    private Long timeoutMs;
}