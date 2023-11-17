package com.tinonino.microservices.core.product.recomendationservice.infra.store.repository;

import com.tinonino.microservices.core.product.recomendationservice.infra.store.entity.RecommendationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntity, String> {
    Flux<RecommendationEntity> findByProductId(int productId);
}
