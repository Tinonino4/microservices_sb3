package com.tinonino.microservices.core.product.recomendationservice.usecase.impl;

import com.tinonino.microservices.core.product.recomendationservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.recomendationservice.infra.rest.model.RecommendationResponse;
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
class GetRecommendationsByProductIdImplTest {
    @Mock
    private ServiceUtil serviceUtil;

    GetRecommendationsByProductIdImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new GetRecommendationsByProductIdImpl(serviceUtil);
    }
    @Test
    void getRecommendationsByProductId1() {
        int productId=1;

        when(serviceUtil.getServiceAddress()).thenReturn("serviceAddress");
        List<RecommendationResponse> result = underTest.execute(productId);

        checkResult(result);
    }

    @Test
    void getRecommendationsByProductIdInvalid() {
        int productId = 0;
        InvalidInputException exception = assertThrows(InvalidInputException.class
        , () -> underTest.execute(productId));
        assertEquals("Invalid productId: 0", exception.getMessage());
    }

    private void checkResult(List<RecommendationResponse> result) {
        List<RecommendationResponse> expected = createExpectedResult();
        assertEquals(expected.size(), result.size());
    }

    private List<RecommendationResponse> createExpectedResult() {
        List<RecommendationResponse> list = new ArrayList<>();
        list.add(new RecommendationResponse(1, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
        list.add(new RecommendationResponse(1, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()));
        list.add(new RecommendationResponse(1, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress()));
        return  list;
    }
}