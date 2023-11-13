package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.utils.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

@Component
public class ProductFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFacade.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceUrl;

    @Autowired
    public ProductFacade(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products";
    }

    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {

            switch (HttpStatus.resolve(ex.getStatusCode().value())) {
                case NOT_FOUND:
                    throw new ProductNotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }
    public Product createProduct(Product body) {

        try {
            String url = productServiceUrl;
            LOG.debug("Will post a new product to URL: {}", url);
            Product product = restTemplate.postForObject(url, body, Product.class);
            LOG.debug("Created a product with id: {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the deleteProduct API on URL: {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode().value()))) {
            case NOT_FOUND:
                return new ProductNotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
