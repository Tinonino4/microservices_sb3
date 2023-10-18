package com.tinonino.microservices.core.product.recomendationservice.domain;

import java.util.Objects;

public class Recommendation {
    int productId;
    int recommendationId;
    String author;
    int rate;
    String content;
    String serviceAddress;

    public Recommendation(int productId, int recommendationId, String author, int rate, String content, String serviceAddress) {
        this.productId = productId;
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recommendation that = (Recommendation) o;

        if (productId != that.productId) return false;
        if (recommendationId != that.recommendationId) return false;
        if (rate != that.rate) return false;
        if (!Objects.equals(author, that.author)) return false;
        if (!Objects.equals(content, that.content)) return false;
        return Objects.equals(serviceAddress, that.serviceAddress);
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + recommendationId;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + rate;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (serviceAddress != null ? serviceAddress.hashCode() : 0);
        return result;
    }
}
