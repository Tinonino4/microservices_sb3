package com.tinonino.microservices.core.product.recomendationservice.infra.rest;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RecommendationController {
    Mono<Recommendation> createRecommendation(Recommendation body);

    /**
     * Sample usage: "curl $HOST:$PORT/recommendation?productId=1".
     *
     * @param productId Id of the product
     * @return the recommendations of the product
     */
    @GetMapping(
            value = "/recommendations",
            produces = "application/json")
    Flux<Recommendation> getRecommendations(
            @RequestParam(value = "productId", required = true) int productId);

    Mono<Void> deleteRecommendations(int productId);}
