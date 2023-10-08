package com.tinonino.microservices.core.product.recomendationservice.usecase.impl;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import com.tinonino.microservices.core.product.recomendationservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationRequest;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationResponse;
import com.tinonino.microservices.core.product.recomendationservice.usecase.GetRecommendationsByProductId;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRecommendationsByProductIdImpl implements GetRecommendationsByProductId {

    private static final Logger LOG = LoggerFactory.getLogger(GetRecommendationsByProductIdImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public GetRecommendationsByProductIdImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }
    @Override
    public List<RecommendationResponse> execute(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 113) {
            LOG.debug("No recommendations found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<RecommendationResponse> list = new ArrayList<>();
        list.add(new RecommendationResponse(productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
        list.add(new RecommendationResponse(productId, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()));
        list.add(new RecommendationResponse(productId, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/recommendation response size: {}", list.size());

        return list;
    }
}
