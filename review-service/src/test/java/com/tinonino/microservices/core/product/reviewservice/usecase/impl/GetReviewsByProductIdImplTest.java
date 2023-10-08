package com.tinonino.microservices.core.product.reviewservice.usecase.impl;

import com.tinonino.microservices.core.product.reviewservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.reviewservice.infra.rest.model.ReviewResponse;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetReviewsByProductIdImplTest {
    @Mock
    private ServiceUtil serviceUtil;
    GetReviewsByProductIdImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new GetReviewsByProductIdImpl(serviceUtil);
    }

    @Test
    void getReviewsByProductId1() {
        int productId = 1;
        when(serviceUtil.getServiceAddress()).thenReturn("serviceAddress");
        List<ReviewResponse> result = underTest.execute(productId);
        checkResult(result);
    }

    private void checkResult(List<ReviewResponse> result) {
        List<ReviewResponse> expected = createExpectedResult();
        assertEquals(expected.size(), result.size());
    }

    private List<ReviewResponse> createExpectedResult() {
        List<ReviewResponse> list = new ArrayList<>();
        list.add(new ReviewResponse(1, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new ReviewResponse(1, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new ReviewResponse(1, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));
        return  list;
    }

    @Test
    void getRecommendationsByProductIdInvalid() {
        int productId = 0;
        InvalidInputException exception = assertThrows(InvalidInputException.class
                , () -> underTest.execute(productId));
        assertEquals("Invalid productId: 0", exception.getMessage());
    }
}