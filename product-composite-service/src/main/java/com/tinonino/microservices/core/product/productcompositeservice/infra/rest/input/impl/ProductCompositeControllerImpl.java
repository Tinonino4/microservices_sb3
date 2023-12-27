package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.impl;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.ProductCompositeController;
import com.tinonino.microservices.core.product.productcompositeservice.usecase.ProductCompositeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);
    private final ProductCompositeService productCompositeService;

    @Autowired
    public ProductCompositeControllerImpl(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;
    }

    @Override
    public Mono<Void> createProduct(ProductAggregate productAggregate) {
        return productCompositeService.createProduct(productAggregate);
    }

    @Override
    public Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent) {
        return productCompositeService.getProduct(productId, delay, faultPercent);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        return productCompositeService.deleteProduct(productId);
    }
}
