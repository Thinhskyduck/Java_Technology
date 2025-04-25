package com.example.lab9and10.controller;

import com.example.lab9and10.dto.OrderRequest;
import com.example.lab9and10.dto.OrderResponse;
import com.example.lab9and10.entity.Product;
import com.example.lab9and10.security.CustomUserDetailsService;
import com.example.lab9and10.security.JwtTokenProvider; // Import nếu cần
import com.example.lab9and10.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // Quan trọng
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class) // Chỉ test OrderController
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // Mock OrderService
    private OrderService orderService;

    // Mock các bean security
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private WebApplicationContext context;

    private OrderRequest validOrderRequest;
    private OrderResponse orderResponse1;
    private OrderResponse.ProductInfo productInfo1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        productInfo1 = new OrderResponse.ProductInfo();
        productInfo1.setId(1L);
        productInfo1.setCode("P001");
        productInfo1.setName("Product 1");
        productInfo1.setPrice(new BigDecimal("10.00"));

        validOrderRequest = new OrderRequest();
        validOrderRequest.setTotalSellingPrice(new BigDecimal("10.00"));
        validOrderRequest.setProductIds(Set.of(1L));

        orderResponse1 = new OrderResponse();
        orderResponse1.setId(1L);
        orderResponse1.setOrderNumber("ORD-12345");
        orderResponse1.setTotalSellingPrice(new BigDecimal("10.00"));
        orderResponse1.setProducts(Set.of(productInfo1));
        orderResponse1.setCreatedAt(LocalDateTime.now().minusDays(1));
        orderResponse1.setUpdatedAt(LocalDateTime.now());
    }

    // --- Test các endpoint (tất cả đều cần xác thực) ---

    @Test
    @DisplayName("GET /api/orders - Lấy tất cả đơn hàng thành công")
    @WithMockUser // Giả lập user đã đăng nhập
    void getAllOrders_Success_Authenticated() throws Exception {
        List<OrderResponse> orders = Collections.singletonList(orderResponse1);
        given(orderService.getAllOrders()).willReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-12345"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    @DisplayName("GET /api/orders - Không được phép (Chưa xác thực)")
    void getAllOrders_Failure_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());

        verify(orderService, never()).getAllOrders();
    }


    @Test
    @DisplayName("GET /api/orders/{id} - Lấy chi tiết đơn hàng thành công")
    @WithMockUser
    void getOrderById_Success_Authenticated() throws Exception {
        given(orderService.getOrderById(1L)).willReturn(Optional.of(orderResponse1));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345"))
                .andExpect(jsonPath("$.products[0].id").value(1L)); // Kiểm tra product trong đơn hàng

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Không tìm thấy đơn hàng")
    @WithMockUser
    void getOrderById_NotFound_Authenticated() throws Exception {
        given(orderService.getOrderById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(99L);
    }


    @Test
    @DisplayName("POST /api/orders - Tạo đơn hàng thành công")
    @WithMockUser
    void createOrder_Success_Authenticated() throws Exception {
        given(orderService.createOrder(any(OrderRequest.class))).willReturn(orderResponse1);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345"));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    @DisplayName("POST /api/orders - Tạo đơn hàng thất bại (Ví dụ: Product không tồn tại)")
    @WithMockUser
    void createOrder_Failure_ProductNotFound() throws Exception {
        given(orderService.createOrder(any(OrderRequest.class)))
                .willThrow(new IllegalArgumentException("Không tìm thấy sản phẩm với IDs: [99]"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderRequest))) // Dữ liệu request vẫn hợp lệ về cấu trúc
                .andExpect(status().isBadRequest()) // Mong đợi lỗi 400
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy sản phẩm"))); // Kiểm tra message lỗi

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    @DisplayName("PUT /api/orders/{id} - Cập nhật đơn hàng thành công")
    @WithMockUser
    void updateOrder_Success_Authenticated() throws Exception {
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setTotalSellingPrice(new BigDecimal("12.00"));
        updateRequest.setProductIds(Set.of(1L)); // Giữ nguyên product

        OrderResponse updatedResponse = new OrderResponse(); // Tạo response giả lập sau khi update
        updatedResponse.setId(1L);
        updatedResponse.setOrderNumber("ORD-12345"); // Giữ nguyên number
        updatedResponse.setTotalSellingPrice(new BigDecimal("12.00")); // Giá mới
        updatedResponse.setProducts(Set.of(productInfo1));
        updatedResponse.setUpdatedAt(LocalDateTime.now());

        given(orderService.updateOrder(eq(1L), any(OrderRequest.class))).willReturn(Optional.of(updatedResponse));

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSellingPrice").value(12.00));

        verify(orderService, times(1)).updateOrder(eq(1L), any(OrderRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Xóa đơn hàng thành công")
    @WithMockUser
    void deleteOrder_Success_Authenticated() throws Exception {
        given(orderService.deleteOrder(1L)).willReturn(true);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Không tìm thấy đơn hàng để xóa")
    @WithMockUser
    void deleteOrder_NotFound_Authenticated() throws Exception {
        given(orderService.deleteOrder(99L)).willReturn(false);

        mockMvc.perform(delete("/api/orders/99"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(99L);
    }
}