package com.tinonino.microservices.core.product.productservice.usecase;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<Product> createProduct(Product product);
    Mono<Product> getProduct(int productId);

    Mono<Void> deleteProduct(int productId);
}
