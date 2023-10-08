package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews?productId=";
    }
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;

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
}
