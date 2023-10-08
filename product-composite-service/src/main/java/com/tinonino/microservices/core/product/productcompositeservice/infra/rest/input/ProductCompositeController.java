package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductCompositeController {
    /**
     * Sample usage: "curl $HOST:$PORT/product-composite/1".
     *
     * @param productId Id of the product
     * @return the composite product info, if found, else null
     */
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json")
    ProductAggregate getProduct(@PathVariable int productId);
}
