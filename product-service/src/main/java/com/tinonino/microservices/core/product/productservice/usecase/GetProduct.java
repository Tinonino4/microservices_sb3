package com.tinonino.microservices.core.product.productservice.usecase;

import com.tinonino.microservices.core.product.productservice.domain.Product;

public interface GetProduct {

    Product execute(int productId);
}
