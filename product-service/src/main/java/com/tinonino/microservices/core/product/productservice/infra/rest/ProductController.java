package com.tinonino.microservices.core.product.productservice.infra.rest;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductController {
    /**
     * Sample usage, see below.
     *
     * curl -X POST $HOST:$PORT/product \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","weight":123}'
     *
     * @param body A JSON representation of the new product
     * @return A JSON representation of the newly created product
     */
    @PostMapping(
            value    = "/products",
            consumes = "application/json",
            produces = "application/json")
    Mono<Product> createProduct(@RequestBody Product body);

    /**
     * Sample usage: "curl $HOST:$PORT/product/1".
     *
     * @param productId Id of the product
     * @return the product, if found, else null
     */
    @GetMapping(
            value = "/products/{productId}",
            produces = "application/json")
    Mono<Product> getProduct(@PathVariable int productId);

    /**
     * Sample usage: "curl -X DELETE $HOST:$PORT/product/1".
     *
     * @param productId Id of the product
     */
    @DeleteMapping(value = "/products/{productId}")
    Mono<Void> deleteProduct(@PathVariable int productId);
}
