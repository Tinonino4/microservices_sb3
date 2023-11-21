package com.tinonino.microservices.core.product.reviewservice.usecase.impl;

import com.tinonino.microservices.core.product.reviewservice.domain.Review;
import com.tinonino.microservices.core.product.reviewservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.reviewservice.infra.store.entity.ReviewEntity;
import com.tinonino.microservices.core.product.reviewservice.infra.store.repository.ReviewRepository;
import com.tinonino.microservices.core.product.reviewservice.usecase.ReviewService;
import com.tinonino.microservices.core.product.reviewservice.usecase.mapper.ReviewMapper;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

import static java.util.logging.Level.FINE;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final Scheduler jdbcScheduler;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil,
                             ReviewRepository repository,
                             @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<Review> createReview(Review review) {
        if (review.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + review.getProductId());
        }
        return Mono.fromCallable(() -> internalCreateReview(review))
                .subscribeOn(jdbcScheduler);
    }

    private Review internalCreateReview(Review body) {
        try {
            ReviewEntity entity = ReviewMapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);
            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return ReviewMapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public Flux<Review> getReviewsByProductId(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOG.info("Will get reviews for product with id={}", productId);
        return Mono.fromCallable(() -> internalGetReviews(productId))
                .flatMapMany(Flux::fromIterable)
                .log(LOG.getName(), FINE)
                .subscribeOn(jdbcScheduler);
    }

    private List<Review> internalGetReviews(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = ReviewMapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("Response size: {}", list.size());
        return list;
    }

    @Override
    public Mono<Void> deleteReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return Mono.fromRunnable(() -> internalDeleteReviews(productId)).subscribeOn(jdbcScheduler).then();
    }

    private void internalDeleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
