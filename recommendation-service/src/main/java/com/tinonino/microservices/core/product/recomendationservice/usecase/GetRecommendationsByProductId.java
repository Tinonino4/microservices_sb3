package com.tinonino.microservices.core.product.recomendationservice.usecase;

import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationRequest;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationResponse;

import java.util.List;

public interface GetRecommendationsByProductId {
    List<RecommendationResponse> execute(int productId);
}
