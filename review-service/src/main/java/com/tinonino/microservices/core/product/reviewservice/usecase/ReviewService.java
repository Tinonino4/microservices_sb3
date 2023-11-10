package com.tinonino.microservices.core.product.reviewservice.usecase;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    List<Review> getReviewsByProductId(int productId);
    void deleteReviews(int productId);
}
