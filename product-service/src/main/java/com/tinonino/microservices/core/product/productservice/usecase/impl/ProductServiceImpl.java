package com.tinonino.microservices.core.product.productservice.usecase.impl;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.product.productservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.product.productservice.infra.store.entity.ProductEntity;
import com.tinonino.microservices.core.product.productservice.infra.store.repository.ProductRepository;
import com.tinonino.microservices.core.product.productservice.usecase.ProductService;
import com.tinonino.microservices.core.product.productservice.usecase.mapper.ProductMapper;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;
    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil,
                              ProductRepository repository,
                              ProductMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product createProduct(Product product) {
        try {
            ProductEntity entity = mapper.apiToEntity(product);
            ProductEntity newEntity = repository.save(entity);
            LOG.debug("createProduct: entity created for productId: {}", product.getProductId());
            return mapper.entityToApi(newEntity);
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + product.getProductId());
        }
    }
    @Override
    public Product getProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        ProductEntity entity = repository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("No product found for productId: " + productId));
        Product response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());
        LOG.debug("getProduct: found productId: {}", response.getProductId());
        return response;
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}
