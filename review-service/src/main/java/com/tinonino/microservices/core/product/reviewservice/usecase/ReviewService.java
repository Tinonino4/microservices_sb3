package com.tinonino.microservices.core.product.reviewservice.usecase;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewService {
    Mono<Review> createReview(Review review);
    Flux<Review> getReviewsByProductId(int productId);
    Mono<Void> deleteReviews(int productId);
}
