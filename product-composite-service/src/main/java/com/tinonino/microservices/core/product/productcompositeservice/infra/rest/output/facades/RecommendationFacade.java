package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
                "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendations?productId=";
    }


    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + productId;

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
}
