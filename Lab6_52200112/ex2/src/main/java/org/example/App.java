package org.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Product product1a = context.getBean("product1", Product.class);
        Product product1b = context.getBean("product1", Product.class);

        Product product2a = context.getBean("product2", Product.class);
        Product product2b = context.getBean("product2", Product.class);

        Product product3a = context.getBean("product3", Product.class);
        Product product3b = context.getBean("product3", Product.class);

        System.out.println("product1a: " + product1a);
        System.out.println("product1b: " + product1b);
        System.out.println("product1 same? " + (product1a == product1b)); // false

        System.out.println("product2a: " + product2a);
        System.out.println("product2b: " + product2b);
        System.out.println("product2 same? " + (product2a == product2b)); // false

        System.out.println("product3a: " + product3a);
        System.out.println("product3b: " + product3b);
        System.out.println("product3 same? " + (product3a == product3b)); // true
    }
}
