package com.tinonino.microservices.core.product.productservice.infra.rest.impl;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.product.productservice.infra.rest.ProductController;
import com.tinonino.microservices.core.product.productservice.usecase.GetProductById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductControllerImpl implements ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);

    private final GetProductById getProductByIdUseCase;

    @Autowired
    public ProductControllerImpl(GetProductById getProductByIdUseCase) {
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);
        return getProductByIdUseCase.execute(productId);
    }
}
