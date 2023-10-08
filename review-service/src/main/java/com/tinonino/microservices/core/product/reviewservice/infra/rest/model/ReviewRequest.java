package com.tinonino.microservices.core.product.reviewservice.infra.rest.model;

public record ReviewRequest(
    int productId,
     int reviewId,
     String author,
     String subject,
     String content,
     String serviceAddress) {
}
