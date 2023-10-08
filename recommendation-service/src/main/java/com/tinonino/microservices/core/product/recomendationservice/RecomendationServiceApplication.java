package com.tinonino.microservices.core.product.recomendationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.tinonino")
public class RecomendationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecomendationServiceApplication.class, args);
	}

}
