package com.example.lab9and10.repository;

import com.example.lab9and10.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
}