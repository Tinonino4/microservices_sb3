package com.tinonino.microservices.core.product.recomendationservice.infra.rest.model;

public record RecommendationResponse(
    int productId,
    int recommendationId,
    String author,
    int rate,
    String content,
    String serviceAddress
) {
}
