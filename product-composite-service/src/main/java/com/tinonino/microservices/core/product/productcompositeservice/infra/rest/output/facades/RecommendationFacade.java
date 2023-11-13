package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Recommendation;
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

@Component
public class RecommendationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendationFacade.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String recommendationServiceUrl;

    @Autowired
    public RecommendationFacade(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        recommendationServiceUrl =
                "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendations";
    }

    public Recommendation createRecommendation(Recommendation body) {

        try {
            String url = recommendationServiceUrl;
            LOG.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.productId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
                    .getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (HttpStatus.resolve(ex.getStatusCode().value())) {

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
