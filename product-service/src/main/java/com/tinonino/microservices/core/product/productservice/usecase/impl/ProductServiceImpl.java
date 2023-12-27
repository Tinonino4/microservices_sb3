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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

import static java.util.logging.Level.FINE;

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
    public Mono<Product> createProduct(Product product) {
        if (product.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + product.getProductId());
        }

        ProductEntity entity = mapper.apiToEntity(product);
        Mono<Product> newEntity = repository.save(entity)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + product.getProductId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity;
    }
    @Override
    public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.info("Will get product info for id={}", productId);

        return repository.findByProductId(productId)
                .map(e -> throwErrorIfBadLuck(e, faultPercent))
                .delayElement(Duration.ofSeconds(delay))
                .switchIfEmpty(Mono.error(new ProductNotFoundException("No product found for productId: " + productId)))
                .log(LOG.getName(), FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        return repository.findByProductId(productId)
                .log(LOG.getName(), FINE)
                .map(repository::delete).flatMap(e -> e);
    }

    private Product setServiceAddress(Product e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }

    private ProductEntity throwErrorIfBadLuck(ProductEntity entity, int faultPercent) {

        if (faultPercent == 0) {
            return entity;
        }

        int randomThreshold = getRandomNumber(1, 100);

        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.info("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }

        return entity;
    }

    private final Random randomNumberGenerator = new Random();

    private int getRandomNumber(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
