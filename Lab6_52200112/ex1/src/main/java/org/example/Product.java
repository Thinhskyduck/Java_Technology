package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Product {
    private String name;
    private double price;

    public Product(Product product, double price) {
        this.name = product.getName();
        this.price = price;
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}