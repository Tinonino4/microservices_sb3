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

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private final RecommendationRepository repository;
    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(RecommendationRepository repository, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Recommendation createRecommendation(Recommendation recommendation) {
        try {
            RecommendationEntity entity = RecommendationMapper.apiToEntity(recommendation);
            RecommendationEntity newEntity = repository.save(entity);
            LOG.debug("createRecommendation: created a recommendation entity: {}/{}",
                    recommendation.getProductId(),
                    recommendation.getRecommendationId()
            );
            return RecommendationMapper.entityToApi(newEntity);
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " +
                    recommendation.getProductId() + ", Recommendation Id:" +
                    recommendation.getRecommendationId()
            );
        }
    }

    @Override
    public List<Recommendation> getRecommendationsByProductId(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        List<RecommendationEntity> entityList = repository.findByProductId(productId);
        List<Recommendation> list = RecommendationMapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getRecommendations: response size: {}", list.size());
        return list;
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOG.debug(
                "deleteRecommendations: tries to delete recommendations for the product with productId: {}",
                productId
        );
        repository.deleteAll(repository.findByProductId(productId));
    }
}
