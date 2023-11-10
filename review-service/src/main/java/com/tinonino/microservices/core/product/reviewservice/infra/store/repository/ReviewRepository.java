package com.tinonino.microservices.core.product.reviewservice.infra.store.repository;

import com.tinonino.microservices.core.product.reviewservice.infra.store.entity.ReviewEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {

    @Transactional(readOnly = true)
    List<ReviewEntity> findByProductId(int productId);
}
