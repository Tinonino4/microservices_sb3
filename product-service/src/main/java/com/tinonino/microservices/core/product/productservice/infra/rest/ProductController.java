package com.tinonino.microservices.core.product.productservice.infra.rest;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductController {
    Mono<Product> createProduct(Product body);

    /**
     * Sample usage: "curl $HOST:$PORT/product/1".
     *
     * @param productId Id of the product
     * @return the product, if found, else null
     */
    @GetMapping(
            value = "/products/{productId}",
            produces = "application/json")
    Mono<Product> getProduct(
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    );

    Mono<Void> deleteProduct(int productId);
}
