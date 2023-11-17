package com.tinonino.microservices.core.product.recomendationservice.infra.rest.impl;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.RecommendationController;
import com.tinonino.microservices.core.product.recomendationservice.usecase.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RecommendationControllerImpl implements RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationControllerImpl(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation body) {
        return recommendationService.createRecommendation(body);
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        return recommendationService.getRecommendationsByProductId(productId);
    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        return recommendationService.deleteRecommendations(productId);
    }
}
