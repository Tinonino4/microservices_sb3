package com.tinonino.microservices.core.utils.http;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record HttpErrorInfo(
        String path,
        String message,
        HttpStatus statusCode,
        LocalDateTime localDateTime
) {
}