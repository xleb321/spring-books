package com.example.service_two.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Служба автоматической подписи данных
 * Периодически проверяет Redis на наличие новых данных и автоматически их подписывает
 */
@Service
public class AutoSignatureService {

    private static final Logger log = LoggerFactory.getLogger(AutoSignatureService.class);

    private final RedisService redisService;
    private final SignatureService signatureService;

    private final AtomicBoolean autoProcessingEnabled = new AtomicBoolean(true);
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public AutoSignatureService(RedisService redisService, SignatureService signatureService) {
        this.redisService = redisService;
        this.signatureService = signatureService;
        log.info("AutoSignatureService initialized. Auto-processing: {}", autoProcessingEnabled.get());
    }

    /**
     * Автоматическая проверка и обработка данных каждые 5 секунд
     */
    @Scheduled(fixedRate = 5000) // Каждые 5 секунд
    public void autoProcessData() {
        if (!autoProcessingEnabled.get() || isProcessing.get()) {
            return;
        }

        try {
            isProcessing.set(true);

            if (redisService.isDataAvailable()) {
                log.info("Auto-detected data in Redis, starting signature process...");
                boolean result = processAvailableData();

                if (result) {
                    log.info("Auto-signature completed successfully");
                } else {
                    log.warn("Auto-signature process failed");
                }
            }

        } catch (Exception e) {
            log.error("Error in auto-signature process: {}", e.getMessage(), e);
        } finally {
            isProcessing.set(false);
        }
    }

    /**
     * Обработка доступных данных
     * @return true если данные были успешно обработаны, false если данных нет или произошла ошибка
     */
    public boolean processAvailableData() {
        if (!redisService.isDataAvailable()) {
            log.debug("No data available for processing");
            return false;
        }

        String dataKey = redisService.getCurrentDataKey();

        // Проверяем, не обрабатывались ли уже эти данные
        if (redisService.isDataProcessed(dataKey)) {
            log.debug("Data already processed, skipping");
            return false;
        }

        try {
            // Получаем данные из Redis
            byte[] data = redisService.getData();
            if (data == null) {
                log.warn("Data became unavailable during processing");
                return false;
            }

            log.info("Processing data of size: {} bytes", data.length);

            // Создаем подпись
            byte[] signature = signatureService.signData(data);

            // Сохраняем подпись в Redis
            redisService.saveSignature(signature);

            // Помечаем данные как обработанные
            redisService.markDataAsProcessed(dataKey);

            // Увеличиваем счетчик обработанных данных
            processedCount.incrementAndGet();

            log.info("Data processed and signed successfully. Total processed: {}", processedCount.get());
            return true;

        } catch (Exception e) {
            log.error("Failed to process and sign data: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Ручная обработка данных (для вызовов из контроллера)
     */
    public boolean processDataManually() {
        log.info("Manual data processing triggered");
        return processAvailableData();
    }

    // Геттеры и сеттеры для управления состоянием

    public boolean isAutoProcessingEnabled() {
        return autoProcessingEnabled.get();
    }

    public void setAutoProcessingEnabled(boolean enabled) {
        boolean previous = autoProcessingEnabled.getAndSet(enabled);
        log.info("Auto-processing {} -> {}", previous, enabled);
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

    public boolean isProcessing() {
        return isProcessing.get();
    }

    /**
     * Сброс счетчика обработанных данных
     */
    public void resetProcessedCount() {
        processedCount.set(0);
        log.info("Processed count reset to 0");
    }
}