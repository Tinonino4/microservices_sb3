package com.tinonino.microservices.core.product.productservice.domain;

import java.util.Objects;

public class Product {
    private final int productId;
    private final String name;
    private final int weight;
    private String serviceAddress;

    public Product() {
        productId = 0;
        name = null;
        weight = 0;
        serviceAddress = null;
    }

    public Product(int productId, String name, int weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
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
        Product product = (Product) o;
        return productId == product.productId && weight == product.weight && Objects.equals(name, product.name) && Objects.equals(serviceAddress, product.serviceAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, weight, serviceAddress);
    }
}
