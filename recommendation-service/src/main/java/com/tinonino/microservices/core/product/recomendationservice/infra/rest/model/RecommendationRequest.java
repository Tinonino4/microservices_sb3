package com.tinonino.microservices.core.product.recomendationservice.infra.rest.model;

public record RecommendationRequest(
    int productId,
    int recommendationId,
    int rate,
    String author,
    String content,
    String serviceAddress
) {
}
