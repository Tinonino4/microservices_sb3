package com.tinonino.microservices.core.product.reviewservice.domain;

public record Review(
    int productId,
     int reviewId,
     String author,
     String subject,
     String content,
     String serviceAddress) {
}
