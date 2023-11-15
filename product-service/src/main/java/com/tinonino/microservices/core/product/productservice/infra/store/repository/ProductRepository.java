package com.tinonino.microservices.core.product.productservice.infra.store.repository;

import com.tinonino.microservices.core.product.productservice.infra.store.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {
    Mono<ProductEntity> findByProductId(int productId);
}
