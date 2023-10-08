package com.tinonino.microservices.core.product.recomendationservice.domain.exception;

public class RecommendationNotFoundException extends RuntimeException {
    public RecommendationNotFoundException() {}

    public RecommendationNotFoundException(String message) {
        super(message);
    }

    public RecommendationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecommendationNotFoundException(Throwable cause) {
        super(cause);
    }
}
