package com.tinonino.microservices.core.product.reviewservice.infra.rest;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewRequest;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewController {
    /**
     * Sample usage: "curl $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     * @return the reviews of the product
     */
    @GetMapping(
            value = "/reviews",
            produces = "application/json")
    List<ReviewResponse> getReviews(@RequestParam(value = "productId", required = true) int productId);
}
