package com.tinonino.microservices.core.product.recomendationservice.usecase.mapper;

import com.tinonino.microservices.core.product.recomendationservice.domain.Recommendation;
import com.tinonino.microservices.core.product.recomendationservice.infra.store.entity.RecommendationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecommendationMapper {
    public static Recommendation entityToApi(RecommendationEntity entity) {
        return new Recommendation(
                entity.getProductId(),
                entity.getRecommendationId(),
                entity.getAuthor(),
                entity.getRating(),
                entity.getContent(),
                null
        );
    }
    public static RecommendationEntity apiToEntity(Recommendation api) {
        return new RecommendationEntity(
                api.getProductId(),
                api.getRecommendationId(),
                api.getAuthor(),
                api.getRate(),
                api.getContent()
        );
    }
    public static List<Recommendation> entityListToApiList(List<RecommendationEntity> entityList) {
        return entityList.stream()
                .map(RecommendationMapper::entityToApi)
                .collect(Collectors.toList());
    }

    public static List<RecommendationEntity> apiListToEntityList(List<Recommendation> apiList) {
        return apiList.stream()
                .map(RecommendationMapper::apiToEntity)
                .collect(Collectors.toList());
    }
}
