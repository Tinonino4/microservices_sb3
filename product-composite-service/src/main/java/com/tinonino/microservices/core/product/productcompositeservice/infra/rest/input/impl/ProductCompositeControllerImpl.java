package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.impl;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.ProductCompositeController;
import com.tinonino.microservices.core.product.productcompositeservice.usecase.ProductCompositeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);
    private final ProductCompositeService productCompositeService;

    @Autowired
    public ProductCompositeControllerImpl(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;
    }

    @Override
    public void createProduct(ProductAggregate productAggregate) {
        productCompositeService.createProduct(productAggregate);
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        return productCompositeService.getProduct(productId);
    }

    @Override
    public void deleteProduct(int productId) {
        productCompositeService.deleteProduct(productId);
    }
}
