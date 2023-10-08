package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output;

import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("products")
public interface ProductClient {
    @GetMapping(value="/product/{productId}")
    Product getProduct(@PathVariable("productId") int productId);
}
