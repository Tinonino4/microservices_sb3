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
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;

@Service
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
    private final SecurityContext nullSecCtx = new SecurityContextImpl();
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
    public Mono<Void> createProduct(ProductAggregate productAggregate) {
        try {
            List<Mono> monoList = new ArrayList<>();
            monoList.add(getLogAuthorizationInfoMono());
            LOG.info("Will create a new composite entity for product.id: {}", productAggregate.getProductId());

            Product product = new Product(productAggregate.getProductId(), productAggregate.getName(), productAggregate.getWeight(), null);
            monoList.add(productFacade.createProduct(product));
            if (productAggregate.getRecommendations() != null) {
                productAggregate.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(productAggregate.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    monoList.add(recommendationFacade.createRecommendation(recommendation));
                });
            }

            if (productAggregate.getReviews() != null) {
                productAggregate.getReviews().forEach(r -> {
                    Review review = new Review(productAggregate.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    monoList.add(reviewFacade.createReview(review));
                });
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", productAggregate.getProductId());

            return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                    .doOnError(ex -> LOG.warn("createCompositeProduct failed: {}", ex.toString()))
                    .then();

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    @Override
    public Mono<ProductAggregate> getProduct(int productId) {
        LOG.info("Will get composite product info for product.id={}", productId);
        return Mono.zip(
                        values -> createProductAggregate(
                                (SecurityContext) values[0], (Product) values[1], (List<Recommendation>) values[2], (List<Review>) values[3], serviceUtil.getServiceAddress()),
                        getSecurityContextMono(),
                        productFacade.getProduct(productId),
                        recommendationFacade.getRecommendations(productId).collectList(),
                        reviewFacade.getReviews(productId).collectList())
                .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
                .log(LOG.getName(), FINE);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        try {
            LOG.info("Will delete a product aggregate for product.id: {}", productId);
            return Mono.zip(
                r -> "",
                getLogAuthorizationInfoMono(),
                productFacade.deleteProduct(productId),
                recommendationFacade.deleteRecommendations(productId),
                reviewFacade.deleteReviews(productId))
            .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
            .log(LOG.getName(), FINE).then();
        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    private ProductAggregate createProductAggregate(
            SecurityContext sc,
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {
        logAuthorizationInfo(sc);

        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate(), r.content()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null)  ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject(), r.content()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).serviceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).serviceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }

    private Mono<SecurityContext> getLogAuthorizationInfoMono() {
        return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo(sc));
    }

    private Mono<SecurityContext> getSecurityContextMono() {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }
}
