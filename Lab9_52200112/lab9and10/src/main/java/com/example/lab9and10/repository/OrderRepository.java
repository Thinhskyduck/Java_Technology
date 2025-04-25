package com.example.lab9and10.repository;

import com.example.lab9and10.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Vẫn nên fetch EAGER products khi tìm theo ID để tránh N+1 khi lấy chi tiết đơn hàng
    @Override
    @EntityGraph(attributePaths = "products")
    Optional<Order> findById(Long id);

    // Và fetch EAGER products khi lấy danh sách (có thể ảnh hưởng hiệu năng nếu list quá lớn, cân nhắc DTO projection/pagination)
    @Override
    @EntityGraph(attributePaths = "products")
    List<Order> findAll();
}