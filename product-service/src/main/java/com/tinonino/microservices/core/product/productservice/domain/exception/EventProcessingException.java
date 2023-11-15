package com.tinonino.microservices.core.product.productservice.domain.exception;

public class EventProcessingException extends RuntimeException {
    public EventProcessingException() {
    }

    public EventProcessingException(String message) {
        super(message);
    }

    public EventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventProcessingException(Throwable cause) {
        super(cause);
    }
}
