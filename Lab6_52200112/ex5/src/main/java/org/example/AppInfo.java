package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppInfo {

    @Value("${app.name}")
    private String name;

    @Value("${app.price}")
    private Double price;

    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
    }
}

