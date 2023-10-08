package com.tinonino.microservices.core.product.productcompositeservice.domain.entities;

public record Review(
    int productId,
     int reviewId,
     String author,
     String subject,
     String content,
     String serviceAddress) {
}
