package com.tinonino.microservices.core.product.productcompositeservice.usecase;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;

public interface ProductCompositeService {

    void createProduct(ProductAggregate productAggregate);
    ProductAggregate getProduct(int productId);
    void deleteProduct(int productId);
}
