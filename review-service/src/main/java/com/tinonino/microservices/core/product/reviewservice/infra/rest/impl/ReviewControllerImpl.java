package com.tinonino.microservices.core.product.reviewservice.infra.rest.impl;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.ReviewController;
import com.tinonino.microservices.core.product.reviewservice.usecase.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewControllerImpl(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public Review createReview(Review review) {
        return reviewService.createReview(review);
    }

    @Override
    public List<Review> getReviews(int productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @Override
    public void deleteReviews(int productId) {
        reviewService.deleteReviews(productId);
    }
}
