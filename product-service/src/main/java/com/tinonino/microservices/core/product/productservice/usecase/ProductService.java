package com.tinonino.microservices.core.product.productservice.usecase;

import com.tinonino.microservices.core.product.productservice.domain.Product;

public interface ProductService {

    Product createProduct(Product product);
    Product getProduct(int productId);

    void deleteProduct(int productId);
}
