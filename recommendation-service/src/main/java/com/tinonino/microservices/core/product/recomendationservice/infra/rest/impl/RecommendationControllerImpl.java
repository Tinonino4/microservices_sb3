package com.tinonino.microservices.core.product.recomendationservice.infra.rest.impl;

import com.tinonino.microservices.core.product.recomendationservice.infra.rest.RecommendationController;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationResponse;
import com.tinonino.microservices.core.product.recomendationservice.usecase.GetRecommendationsByProductId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendationControllerImpl implements RecommendationController {
    private final GetRecommendationsByProductId getRecommendationsByProductId;

    @Autowired
    public RecommendationControllerImpl(GetRecommendationsByProductId getRecommendationsByProductId) {
        this.getRecommendationsByProductId = getRecommendationsByProductId;
    }
    @Override
    public List<RecommendationResponse> getRecommendations(int productId) {
        return getRecommendationsByProductId.execute(productId);
    }
}
