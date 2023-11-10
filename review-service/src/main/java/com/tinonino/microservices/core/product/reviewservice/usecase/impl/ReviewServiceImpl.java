package com.tinonino.microservices.core.product.reviewservice.usecase.impl;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import com.tinonino.microservices.core.product.reviewservice.infra.store.entity.ReviewEntity;
import com.tinonino.microservices.core.product.reviewservice.infra.store.repository.ReviewRepository;
import com.tinonino.microservices.core.product.reviewservice.usecase.ReviewService;
import com.tinonino.microservices.core.product.reviewservice.usecase.mapper.ReviewMapper;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil,
                             ReviewRepository repository) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
    }

    @Override
    public Review createReview(Review review) {
        try {
            ReviewEntity entity = ReviewMapper.apiToEntity(review);
            ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}",
                    review.getProductId(),
                    review.getReviewId()
            );
            return ReviewMapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " +
                    review.getProductId() + ", Review Id:" +
                    review.getReviewId()
            );
        }
    }

    @Override
    public List<Review> getReviewsByProductId(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/reviews response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
