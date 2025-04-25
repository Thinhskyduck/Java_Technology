package com.example.lab9and10.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class OrderRequest {
    // orderNumber thường do hệ thống tạo ra khi thêm mới
    private String orderNumber; // Có thể dùng khi cập nhật

    @NotNull(message = "Tổng giá bán không được để trống")
    private BigDecimal totalSellingPrice; // Giả sử được cung cấp để đơn giản

    @NotEmpty(message = "Đơn hàng phải chứa ít nhất một ID sản phẩm")
    private Set<Long> productIds;
}