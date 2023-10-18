package com.tinonino.microservices.core.product.productservice.infra.store.repository;

import com.tinonino.microservices.core.product.productservice.infra.store.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, String>, CrudRepository<ProductEntity, String> {
    Optional<ProductEntity> findByProductId(int productId);
}
