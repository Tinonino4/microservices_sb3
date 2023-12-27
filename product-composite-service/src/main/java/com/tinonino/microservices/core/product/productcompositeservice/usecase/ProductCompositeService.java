package com.tinonino.microservices.core.product.productcompositeservice.usecase;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import reactor.core.publisher.Mono;

public interface ProductCompositeService {

    Mono<Void> createProduct(ProductAggregate productAggregate);
    Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent);
    Mono<Void> deleteProduct(int productId);
}
