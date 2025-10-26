package com.example.service_one.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация WebClient для HTTP вызовов
 */
@Configuration
public class WebClientConfig {

    @Value("${app.service-two.url}")
    private String serviceTwoUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(serviceTwoUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}