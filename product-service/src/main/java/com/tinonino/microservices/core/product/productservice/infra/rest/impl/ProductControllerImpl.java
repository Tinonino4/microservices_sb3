package com.tinonino.microservices.core.product.productservice.infra.rest.impl;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.product.productservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.product.productservice.infra.rest.ProductController;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductControllerImpl implements ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductControllerImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 13) {
            throw new ProductNotFoundException("No product found for productId: " + productId);
        }

        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
}
