package com.tinonino.microservices.core.product.productcompositeservice.domain.entities;

public record Recommendation(
    int productId,
    int recommendationId,
    String author,
    int rate,
    String content,
    String serviceAddress
) {
}
