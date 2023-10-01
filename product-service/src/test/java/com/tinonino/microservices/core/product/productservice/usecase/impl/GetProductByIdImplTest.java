package com.tinonino.microservices.core.product.productservice.usecase.impl;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetProductByIdImplTest {

    @Mock
    private ServiceUtil serviceUtil;

    GetProductByIdImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new GetProductByIdImpl(serviceUtil);
    }
    @Test
    void getProductById() {
        int productId = 1;
        
        when(serviceUtil.getServiceAddress()).thenReturn("serviceAddress");
        Product result = underTest.execute(productId);

        checkResult(result);
    }

    private void checkResult(Product result) {
        Product expected = new Product(1, "name-1", 123, "serviceAddress");
        assertEquals(1, result.getProductId());
        assertEquals("name-1", result.getName());
        assertEquals("serviceAddress", result.getServiceAddress());
        assertEquals(123, result.getWeight());
        assertEquals(expected, result);
    }

}