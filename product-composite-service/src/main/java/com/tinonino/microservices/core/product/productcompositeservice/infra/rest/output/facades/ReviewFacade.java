package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Review;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.utils.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ReviewFacade {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewFacade.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String reviewServiceUrl;

    @Autowired
    public ReviewFacade(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort
    ) {
       this.restTemplate = restTemplate;
       this.mapper = mapper;

        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews";
    }

    public Review createReview(Review body) {

        try {
            String url = reviewServiceUrl;
            LOG.debug("Will post a new review to URL: {}", url);

            Review review = restTemplate.postForObject(url, body, Review.class);
            LOG.debug("Created a review with id: {}", review.productId());

            return review;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;

            LOG.debug("Will call getReviews API on URL: {}", url);
            List<Review> reviews = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {})
                    .getBody();

            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteReviews API on URL: {}", url);

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
