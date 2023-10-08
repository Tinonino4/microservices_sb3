package com.tinonino.microservices.core.product.recomendationservice.domain;

public record Recommendation(
    int productId,
    int recommendationId,
    String author,
    int rate,
    String content,
    String serviceAddress
) {
}
