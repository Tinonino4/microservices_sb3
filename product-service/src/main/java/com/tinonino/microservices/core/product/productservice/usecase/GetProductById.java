package com.tinonino.microservices.core.product.productservice.usecase;

import com.tinonino.microservices.core.product.productservice.domain.Product;

public interface GetProductById {

    Product execute(int productId);
}
