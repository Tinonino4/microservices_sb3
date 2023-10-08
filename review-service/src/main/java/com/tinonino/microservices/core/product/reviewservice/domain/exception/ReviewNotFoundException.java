package com.tinonino.microservices.core.product.reviewservice.domain.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {}

    public ReviewNotFoundException(String message) {
        super(message);
    }

    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReviewNotFoundException(Throwable cause) {
        super(cause);
    }
}
