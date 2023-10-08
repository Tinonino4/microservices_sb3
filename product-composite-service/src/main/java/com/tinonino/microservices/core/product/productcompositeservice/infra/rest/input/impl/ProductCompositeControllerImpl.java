package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.impl;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input.ProductCompositeController;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.ProductClient;
import com.tinonino.microservices.core.product.productcompositeservice.usecase.GetProductCompositeById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);
    private final GetProductCompositeById getProductCompositeById;

    @Autowired
    public ProductCompositeControllerImpl(GetProductCompositeById getProductCompositeById) {
        this.getProductCompositeById = getProductCompositeById;
    }
    @Override
    public ProductAggregate getProduct(int productId) {
        return getProductCompositeById.execute(productId);
    }
}
