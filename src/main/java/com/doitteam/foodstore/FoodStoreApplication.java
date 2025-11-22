package com.doitteam.foodstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FoodStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodStoreApplication.class, args);
		System.out.println("===========================================");
		System.out.println("🍔 Food Store API is running!");
		System.out.println("📍 URL: http://localhost:8080/api");
		System.out.println("📖 Health Check: http://localhost:8080/api/health");
		System.out.println("===========================================");
	}
}