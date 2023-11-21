package com.tinonino.microservices.core.product.reviewservice.infra.rest;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewRequest;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewController {
    Mono<Review> createReview(Review body);

    /**
     * Sample usage: "curl $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     * @return the reviews of the product
     */
    @GetMapping(
            value = "/reviews",
            produces = "application/json")
    Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    Mono<Void> deleteReviews(int productId);
}
