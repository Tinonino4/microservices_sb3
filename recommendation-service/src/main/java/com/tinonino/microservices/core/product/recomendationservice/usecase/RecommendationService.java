package com.tinonino.microservices.core.product.recomendationservice.usecase;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RecommendationService {
    Mono<Recommendation> createRecommendation(Recommendation recommendation);
    Flux<Recommendation> getRecommendationsByProductId(int productId);
    Mono<Void> deleteRecommendations(int productId);
}
