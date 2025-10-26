package com.example.service_one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Главный класс приложения Service One
 * Отвечает за генерацию данных и координацию процесса подписи
 */
@SpringBootApplication
@EnableAsync
public class ServiceOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceOneApplication.class, args);
	}
}