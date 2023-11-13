package com.tinonino.microservices.core.product.reviewservice.infra.rest;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewRequest;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewController {
    /**
     * Sample usage, see below.
     *
     * curl -X POST $HOST:$PORT/review \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"reviewId":456,"author":"me","subject":"yada, yada, yada","content":"yada, yada, yada"}'
     *
     * @param body A JSON representation of the new review
     * @return A JSON representation of the newly created review
     */
    @PostMapping(
            value    = "/reviews",
            consumes = "application/json",
            produces = "application/json")
    Review createReview(@RequestBody Review body);

    /**
     * Sample usage: "curl $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     * @return the reviews of the product
     */
    @GetMapping(
            value = "/reviews",
            produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    /**
     * Sample usage: "curl -X DELETE $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     */
    @DeleteMapping(value = "/reviews")
    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
