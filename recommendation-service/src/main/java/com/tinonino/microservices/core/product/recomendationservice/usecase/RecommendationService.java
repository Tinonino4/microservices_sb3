package com.tinonino.microservices.core.product.recomendationservice.usecase;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;

import java.util.List;

public interface RecommendationService {
    Recommendation createRecommendation(Recommendation recommendation);
    List<Recommendation> getRecommendationsByProductId(int productId);
    void deleteRecommendations(int productId);
}
