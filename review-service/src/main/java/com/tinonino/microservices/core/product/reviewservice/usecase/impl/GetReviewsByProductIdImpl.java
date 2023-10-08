package com.tinonino.microservices.core.product.reviewservice.usecase.impl;

import com.tinonino.microservices.core.product.reviewservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import com.tinonino.microservices.core.product.reviewservice.usecase.GetReviewsByProductId;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetReviewsByProductIdImpl implements GetReviewsByProductId {
    private static final Logger LOG = LoggerFactory.getLogger(GetReviewsByProductIdImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public GetReviewsByProductIdImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<ReviewResponse> execute(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<ReviewResponse> list = new ArrayList<>();
        list.add(new ReviewResponse(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new ReviewResponse(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new ReviewResponse(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/reviews response size: {}", list.size());

        return list;
    }
}
