package com.tinonino.microservices.core.product.recomendationservice.usecase.impl;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import com.tinonino.microservices.core.product.recomendationservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.recomendationservice.infra.store.entity.RecommendationEntity;
import com.tinonino.microservices.core.product.recomendationservice.infra.store.repository.RecommendationRepository;
import com.tinonino.microservices.core.product.recomendationservice.usecase.RecommendationService;
import com.tinonino.microservices.core.product.recomendationservice.usecase.mapper.RecommendationMapper;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.logging.Level.FINE;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private final RecommendationRepository repository;
    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(
            RecommendationRepository repository,
            ServiceUtil serviceUtil) {
        this.repository = repository;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation recommendation) {
        if (recommendation.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + recommendation.getProductId());
        }

        RecommendationEntity entity = RecommendationMapper.apiToEntity(recommendation);
        Mono<Recommendation> newEntity = repository.save(entity)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: "
                                + recommendation.getProductId() + ", Recommendation Id:"
                                + recommendation.getRecommendationId()))
                .map(e -> RecommendationMapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Flux<Recommendation> getRecommendationsByProductId(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOG.info("Will get recommendations for product with id={}", productId);
        return repository.findByProductId(productId)
                .log(LOG.getName(), FINE)
                .map(RecommendationMapper::entityToApi)
                .map(this::setServiceAddress);
    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        return repository.deleteAll(repository.findByProductId(productId));
    }

    private Recommendation setServiceAddress(Recommendation e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}
