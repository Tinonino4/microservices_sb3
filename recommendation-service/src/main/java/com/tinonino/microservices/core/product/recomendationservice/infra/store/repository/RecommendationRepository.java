package com.tinonino.microservices.core.product.recomendationservice.infra.store.repository;

import com.tinonino.microservices.core.product.recomendationservice.infra.store.entity.RecommendationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByProductId(int productId);
}
