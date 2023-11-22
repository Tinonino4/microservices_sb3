package com.tinonino.microservices.core.product.productcompositeservice;

import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ProductFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.RecommendationFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ReviewFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class HealthCheckConfiguration {
    @Autowired
    ProductFacade productFacade;
    @Autowired
    RecommendationFacade recommendationFacade;
    @Autowired
    ReviewFacade reviewFacade;

    @Bean
    ReactiveHealthContributor coreServices() {

        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();

        registry.put("product", () -> productFacade.getProductHealth());
        registry.put("recommendation", () -> recommendationFacade.getRecommendationHealth());
        registry.put("review", () -> reviewFacade.getReviewHealth());

        return CompositeReactiveHealthContributor.fromMap(registry);
    }
}
