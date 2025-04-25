package com.example.lab9and10.service;

import com.example.lab9and10.dto.OrderRequest;
import com.example.lab9and10.dto.OrderResponse;
import com.example.lab9and10.entity.Order;
import com.example.lab9and10.entity.Product;
import com.example.lab9and10.repository.OrderRepository;
import com.example.lab9and10.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream() // findAll đã eager fetch products
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id) // findById đã eager fetch products
                .map(OrderResponse::fromEntity);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Lấy tất cả sản phẩm theo IDs
        Set<Product> products = new HashSet<>(productRepository.findAllById(request.getProductIds()));

        // Kiểm tra xem tất cả ID sản phẩm yêu cầu có được tìm thấy không
        if (products.size() != request.getProductIds().size()) {
            Set<Long> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(request.getProductIds());
            missingIds.removeAll(foundIds);
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với IDs: " + missingIds);
        }

        Order order = new Order();
        // Tính tổng giá (hoặc sử dụng giá được cung cấp - đang dùng giá cung cấp theo DTO)
        // Trong thực tế, bạn nên tính lại tổng giá từ các sản phẩm lấy được để đảm bảo chính xác
        // BigDecimal calculatedTotal = products.stream()
        //                                    .map(Product::getPrice)
        //                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        // order.setTotalSellingPrice(calculatedTotal);
        order.setTotalSellingPrice(request.getTotalSellingPrice());
        order.setProducts(products);
        // orderNumber và timestamps được đặt bởi @PrePersist

        Order savedOrder = orderRepository.save(order);
        // Nạp lại order với products để trả về DTO đầy đủ nếu cần (do save có thể không trả về đầy đủ)
        // Hoặc dùng findById ngay sau save nếu cần đảm bảo
        // Order fetchedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        // return OrderResponse.fromEntity(fetchedOrder);
        return OrderResponse.fromEntity(savedOrder); // Trả về DTO
    }

    @Transactional
    public Optional<OrderResponse> updateOrder(Long id, OrderRequest request) {
        return orderRepository.findById(id) // Lấy order hiện tại (đã có products)
                .map(existingOrder -> {
                    // Lấy tập hợp sản phẩm mới
                    Set<Product> products = new HashSet<>(productRepository.findAllById(request.getProductIds()));
                    if (products.size() != request.getProductIds().size()) {
                        Set<Long> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
                        Set<Long> missingIds = new HashSet<>(request.getProductIds());
                        missingIds.removeAll(foundIds);
                        throw new IllegalArgumentException("Không tìm thấy sản phẩm với IDs để cập nhật: " + missingIds);
                    }

                    existingOrder.setTotalSellingPrice(request.getTotalSellingPrice()); // Cập nhật giá
                    existingOrder.setProducts(products); // Cập nhật danh sách sản phẩm
                    if(request.getOrderNumber() != null && !request.getOrderNumber().isBlank()) {
                        // Có thể thêm kiểm tra trùng orderNumber nếu cho phép cập nhật
                        existingOrder.setOrderNumber(request.getOrderNumber());
                    }
                    // updatedAt được đặt bởi @PreUpdate

                    Order updatedOrder = orderRepository.save(existingOrder);
                    // Tương tự create, có thể cần fetch lại để đảm bảo có đủ thông tin products
                    return OrderResponse.fromEntity(updatedOrder);
                });
    }

    @Transactional
    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}