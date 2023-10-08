package com.tinonino.microservices.core.product.recomendationservice.infra.rest;

import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecommendationController {
    /**
     * Sample usage: "curl $HOST:$PORT/recommendation?productId=1".
     *
     * @param productId Id of the product
     * @return the recommendations of the product
     */
    @GetMapping(
            value = "/recommendations",
            produces = "application/json")
    List<RecommendationResponse> getRecommendations(
            @RequestParam(value = "productId", required = true) int productId);
}
