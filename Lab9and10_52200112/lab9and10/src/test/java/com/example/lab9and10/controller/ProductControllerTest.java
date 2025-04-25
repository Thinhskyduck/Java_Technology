package com.example.lab9and10.controller;

import com.example.lab9and10.dto.ProductRequest;
import com.example.lab9and10.entity.Product;
import com.example.lab9and10.security.CustomUserDetailsService;
import com.example.lab9and10.security.JwtTokenProvider; // Import nếu cần mock trực tiếp
import com.example.lab9and10.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // Rất quan trọng để test endpoint bảo mật
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Nếu CSRF bật (đã tắt)
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Chỉ test ProductController
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // Mock ProductService
    private ProductService productService;

    // Mock các bean security cần thiết mà ProductController có thể gián tiếp cần
    // (Thường không cần mock trực tiếp nếu dùng @WithMockUser)
    @MockBean private JwtTokenProvider jwtTokenProvider;
     @MockBean private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private WebApplicationContext context;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        // Khởi tạo MockMvc với Spring Security context
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) // Quan trọng
                .build();

        product1 = new Product(1L, "P001", "Product 1", new BigDecimal("10.00"), null, "Desc 1");
        product2 = new Product(2L, "P002", "Product 2", new BigDecimal("20.00"), null, "Desc 2");

        productRequest = new ProductRequest();
        productRequest.setCode("P003");
        productRequest.setName("Product 3");
        productRequest.setPrice(new BigDecimal("30.00"));
        productRequest.setDescription("Desc 3");
    }

    // --- Test các endpoint công khai ---

    @Test
    @DisplayName("GET /api/products - Lấy tất cả sản phẩm thành công")
    void getAllProducts_Success() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        given(productService.getAllProducts()).willReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Kiểm tra size của mảng JSON
                .andExpect(jsonPath("$[0].id").value(product1.getId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].id").value(product2.getId()));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Lấy sản phẩm theo ID thành công")
    void getProductById_Success() throws Exception {
        given(productService.getProductById(1L)).willReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()))
                .andExpect(jsonPath("$.price").value(10.00));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Không tìm thấy sản phẩm")
    void getProductById_NotFound() throws Exception {
        given(productService.getProductById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(99L);
    }

    // --- Test các endpoint cần xác thực (@WithMockUser) ---

    @Test
    @DisplayName("POST /api/products - Tạo sản phẩm thành công (Yêu cầu xác thực)")
    @WithMockUser // Giả lập user đã đăng nhập (không cần chỉ rõ role nếu không check role)
    void createProduct_Success_Authenticated() throws Exception {
        Product createdProduct = new Product(3L, "P003", "Product 3", new BigDecimal("30.00"), null, "Desc 3");
        given(productService.createProduct(any(ProductRequest.class))).willReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                        // .with(csrf()) // Bỏ qua nếu CSRF đã disable trong SecurityConfig
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.code").value("P003"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /api/products - Không được phép (Chưa xác thực)")
    void createProduct_Failure_Unauthorized() throws Exception {
        // Không dùng @WithMockUser
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isUnauthorized()); // Mong đợi 401

        verify(productService, never()).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Cập nhật sản phẩm thành công (Yêu cầu xác thực)")
    @WithMockUser
    void updateProduct_Success_Authenticated() throws Exception {
        Product updatedProduct = new Product(1L, "P001-UPD", "Updated Product", new BigDecimal("15.00"), null, "Updated Desc");
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setCode("P001-UPD");
        updateRequest.setName("Updated Product");
        updateRequest.setPrice(new BigDecimal("15.00"));
        updateRequest.setDescription("Updated Desc");

        given(productService.updateProduct(eq(1L), any(ProductRequest.class))).willReturn(Optional.of(updatedProduct));

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(15.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Không tìm thấy sản phẩm (Yêu cầu xác thực)")
    @WithMockUser
    void updateProduct_NotFound_Authenticated() throws Exception {
        ProductRequest updateRequest = new ProductRequest(); // Dữ liệu hợp lệ
        updateRequest.setCode("P999-UPD");
        updateRequest.setName("Updated Product");
        updateRequest.setPrice(new BigDecimal("99.00"));

        given(productService.updateProduct(eq(99L), any(ProductRequest.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(99L), any(ProductRequest.class));
    }


    @Test
    @DisplayName("PATCH /api/products/{id} - Cập nhật một phần thành công (Yêu cầu xác thực)")
    @WithMockUser
    void patchProduct_Success_Authenticated() throws Exception {
        Map<String, Object> updates = Map.of("price", new BigDecimal("12.50"));
        Product patchedProduct = new Product(1L, "P001", "Product 1", new BigDecimal("12.50"), null, "Desc 1"); // Giả sử giá đã cập nhật

        given(productService.patchProduct(eq(1L), anyMap())).willReturn(Optional.of(patchedProduct));

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.50));

        verify(productService, times(1)).patchProduct(eq(1L), anyMap());
    }


    @Test
    @DisplayName("DELETE /api/products/{id} - Xóa sản phẩm thành công (Yêu cầu xác thực)")
    @WithMockUser
    void deleteProduct_Success_Authenticated() throws Exception {
        given(productService.deleteProduct(1L)).willReturn(true); // Giả lập service xóa thành công

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent()); // Mong đợi 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Không tìm thấy sản phẩm (Yêu cầu xác thực)")
    @WithMockUser
    void deleteProduct_NotFound_Authenticated() throws Exception {
        given(productService.deleteProduct(99L)).willReturn(false); // Giả lập service không tìm thấy để xóa

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound()); // Mong đợi 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Không được phép (Chưa xác thực)")
    void deleteProduct_Failure_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isUnauthorized()); // Mong đợi 401

        verify(productService, never()).deleteProduct(anyLong());
    }
}