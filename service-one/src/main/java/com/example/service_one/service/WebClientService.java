package com.example.service_one.service;

import com.example.service_one.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис для HTTP взаимодействия с service-two
 */
@Service
public class WebClientService {

    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);
    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public CompletableFuture<Boolean> triggerSignature() {
        log.debug("Triggering signature process in service-two");

        return webClient.post()
                .uri("/api/v1/sign")
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    log.debug("Signature trigger response: success={}, message={}",
                            response.isSuccess(), response.getMessage());
                    return response.isSuccess();
                })
                .doOnSuccess(success -> {
                    if (success) {
                        log.info("Signature process successfully triggered in service-two");
                    } else {
                        log.warn("Signature process trigger returned failure");
                    }
                })
                .doOnError(WebClientResponseException.class, error -> {
                    log.error("HTTP error triggering signature: {} - {}",
                            error.getStatusCode(), error.getResponseBodyAsString());
                })
                .doOnError(Exception.class, error -> {
                    log.error("Error triggering signature: {}", error.getMessage(), error);
                })
                .defaultIfEmpty(false)
                .toFuture();
    }
}