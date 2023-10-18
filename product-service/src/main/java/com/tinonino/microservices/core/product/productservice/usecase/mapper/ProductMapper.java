package com.tinonino.microservices.core.product.productservice.usecase.mapper;

import com.tinonino.microservices.core.product.productservice.domain.Product;
import com.tinonino.microservices.core.product.productservice.infra.store.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product entityToApi(ProductEntity entity) {
        return new Product(entity.getProductId(), entity.getName(), entity.getWeight(), null);
    }
    public ProductEntity apiToEntity(Product product) {
        return new ProductEntity(product.getProductId(), product.getName(), product.getWeight());
    }
}
