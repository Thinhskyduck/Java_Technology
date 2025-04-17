package com.example.lab7;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab7Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab7Application.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> System.out.println("Chương trình Spring Boot đã chạy thành công!");
	}
}
