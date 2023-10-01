package com.tinonino.microservices.core.product.productservice.usecase.impl;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.product.productservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.product.productservice.usecase.GetProduct;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetProductImpl implements GetProduct {
    private final ServiceUtil serviceUtil;
    @Autowired
    public GetProductImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }
    @Override
    public Product execute(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        if (productId == 13) {
            throw new ProductNotFoundException("No product found for productId: " + productId);
        }
        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
}
