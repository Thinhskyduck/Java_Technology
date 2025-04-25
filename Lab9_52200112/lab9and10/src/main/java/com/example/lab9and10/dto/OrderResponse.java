package com.example.lab9and10.dto;

import com.example.lab9and10.entity.Product; // Import entity Product
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalSellingPrice;
    private Set<ProductInfo> products; // Dùng DTO lồng nhau cho thông tin sản phẩm
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Phương thức factory tĩnh để chuyển đổi (tránh lộ entity)
    public static OrderResponse fromEntity(com.example.lab9and10.entity.Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalSellingPrice(order.getTotalSellingPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        // Chuyển đổi danh sách Product entity sang danh sách ProductInfo DTO
        dto.setProducts(order.getProducts().stream()
                .map(ProductInfo::fromEntity)
                .collect(Collectors.toSet()));
        return dto;
    }

    @Getter
    @Setter
    public static class ProductInfo { // DTO lồng nhau cho chi tiết sản phẩm trong đơn hàng
        private Long id;
        private String code;
        private String name;
        private BigDecimal price;
        // Không nên trả về description, illustration trong list đơn hàng

        public static ProductInfo fromEntity(Product product) {
            ProductInfo dto = new ProductInfo();
            dto.setId(product.getId());
            dto.setCode(product.getCode());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            return dto;
        }
    }
}