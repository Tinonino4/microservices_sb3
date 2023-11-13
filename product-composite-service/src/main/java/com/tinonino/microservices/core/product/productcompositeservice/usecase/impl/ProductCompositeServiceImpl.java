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
import com.tinonino.microservices.core.product.productcompositeservice.usecase.ProductCompositeService;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductFacade productFacade;
    private final RecommendationFacade recommendationFacade;
    private final ReviewFacade reviewFacade;

    @Autowired
    public ProductCompositeServiceImpl(
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
    public void createProduct(ProductAggregate productAggregate) {
        try {
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", productAggregate.getProductId());
            Product product = new Product(productAggregate.getProductId(), productAggregate.getName(), productAggregate.getWeight(), null);
            productFacade.createProduct(product);

            if (productAggregate.getRecommendations() != null) {
                productAggregate.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(productAggregate.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    recommendationFacade.createRecommendation(recommendation);
                });
            }

            if (productAggregate.getReviews() != null) {
                productAggregate.getReviews().forEach(r -> {
                    Review review = new Review(productAggregate.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    reviewFacade.createReview(review);
                });
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", productAggregate.getProductId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = productFacade.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException("No product found for productId: " + productId);
        }
        List<Recommendation> recommendations = recommendationFacade.getRecommendations(productId);
        List<Review> reviews = reviewFacade.getReviews(productId);
        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        productFacade.deleteProduct(productId);
        recommendationFacade.deleteRecommendations(productId);
        reviewFacade.deleteReviews(productId);
        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
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
                        .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate(), r.content()))
                        .collect(Collectors.toList());
        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries =
                (reviews == null) ? null : reviews.stream()
                        .map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject(), r.content()))
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
