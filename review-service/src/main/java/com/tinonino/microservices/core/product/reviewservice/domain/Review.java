package com.tinonino.microservices.core.product.reviewservice.domain;

import java.util.Objects;

public class Review {
    int productId;
     int reviewId;
     String author;
     String subject;
     String content;
     String serviceAddress;

    public Review(int productId, int reviewId, String author, String subject, String content, String serviceAddress) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

        Review review = (Review) o;

        if (productId != review.productId) return false;
        if (reviewId != review.reviewId) return false;
        if (!Objects.equals(author, review.author)) return false;
        if (!Objects.equals(subject, review.subject)) return false;
        if (!Objects.equals(content, review.content)) return false;
        return Objects.equals(serviceAddress, review.serviceAddress);
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + reviewId;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (serviceAddress != null ? serviceAddress.hashCode() : 0);
        return result;
    }
}