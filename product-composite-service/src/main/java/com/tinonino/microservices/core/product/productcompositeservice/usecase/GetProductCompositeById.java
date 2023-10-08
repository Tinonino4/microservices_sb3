package com.tinonino.microservices.core.product.productcompositeservice.usecase;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;

public interface GetProductCompositeById {
    ProductAggregate execute(int productId);
}
