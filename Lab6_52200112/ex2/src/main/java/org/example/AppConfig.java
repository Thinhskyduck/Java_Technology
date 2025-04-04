package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public Product product1() {
        Product p = new Product();
        p = new Product("Laptop", 1200.0);
        return p;
    }

    @Bean
    @Scope("prototype")
    public Product product2() {
        return new Product("Smartphone", 800.0);
    }

    @Bean
    @Scope("singleton")
    public Product product3() {
        return new Product("Tablet", 500.0);
    }
}
