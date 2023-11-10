package com.tinonino.microservices.core.product.reviewservice.usecase.mapper;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.infra.store.entity.ReviewEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {
    public static Review entityToApi(ReviewEntity entity) {
        return new Review(
                entity.getProductId(),
                entity.getReviewId(),
                entity.getAuthor(),
                entity.getSubject(),
                entity.getContent(),
                null
        );
    }
    public static ReviewEntity apiToEntity(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.setProductId(review.getProductId());
        entity.setReviewId(review.getReviewId());
        entity.setAuthor(review.getAuthor());
        entity.setSubject(review.getSubject());
        entity.setContent(review.getContent());
        // You might want to set serviceAddress here if it's needed in ReviewEntity
        return entity;
    }
    public static List<Review> entityListToApiList(List<ReviewEntity> entityList) {
        return entityList.stream()
                .map(ReviewMapper::entityToApi)
                .collect(Collectors.toList());
    }
    public static List<ReviewEntity> apiListToEntityList(List<Review> reviewList) {
        return reviewList.stream()
                .map(ReviewMapper::apiToEntity)
                .collect(Collectors.toList());
    }
}
