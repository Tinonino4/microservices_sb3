package com.tinonino.microservices.core.product.reviewservice.infra.rest.impl;

import com.tinonino.microservices.core.product.reviewservice.infra.rest.ReviewController;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import com.tinonino.microservices.core.product.reviewservice.usecase.GetReviewsByProductId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewControllerImpl implements ReviewController {

    private final GetReviewsByProductId getReviewsByProductId;

    @Autowired
    public ReviewControllerImpl(GetReviewsByProductId getReviewsByProductId) {
        this.getReviewsByProductId = getReviewsByProductId;
    }
    @Override
    public List<ReviewResponse> getReviews(int productId) {
        return getReviewsByProductId.execute(productId);
    }
}
