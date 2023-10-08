package com.tinonino.microservices.core.product.productcompositeservice.usecase.impl;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import com.tinonino.microservices.core.product.productcompositeservice.domain.RecommendationSummary;
import com.tinonino.microservices.core.product.productcompositeservice.domain.ReviewSummary;
import com.tinonino.microservices.core.product.productcompositeservice.domain.ServiceAddresses;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Recommendation;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Review;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ProductFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.RecommendationFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ReviewFacade;
import com.tinonino.microservices.core.product.productcompositeservice.usecase.GetProductCompositeById;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetProductCompositeByIdImpl implements GetProductCompositeById {
    private static final Logger LOG = LoggerFactory.getLogger(GetProductCompositeByIdImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductFacade productFacade;
    private final RecommendationFacade recommendationFacade;
    private final ReviewFacade reviewFacade;

    @Autowired
    public GetProductCompositeByIdImpl(
            ServiceUtil serviceUtil,
            ProductFacade productFacade,
            RecommendationFacade recommendationFacade,
            ReviewFacade reviewFacade) {
        this.serviceUtil = serviceUtil;
        this.productFacade = productFacade;
        this.reviewFacade = reviewFacade;
        this.recommendationFacade = recommendationFacade;
    }
    @Override
    public ProductAggregate execute(int productId) {
        Product product = productFacade.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException("No product found for productId: " + productId);
        }
        List<Recommendation> recommendations = recommendationFacade.getRecommendations(productId);
        List<Review> reviews = reviewFacade.getReviews(productId);
        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {
        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();
        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries =
                (recommendations == null) ? null : recommendations.stream()
                        .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate()))
                        .collect(Collectors.toList());
        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries =
                (reviews == null) ? null : reviews.stream()
                        .map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject()))
                        .collect(Collectors.toList());
        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).serviceAddress() : "";
        String recommendationAddress =
                (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).serviceAddress() : "";
        ServiceAddresses serviceAddresses =
                new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(
                productId,
                name,
                weight,
                recommendationSummaries,
                reviewSummaries,
                serviceAddresses
        );
    }
}
