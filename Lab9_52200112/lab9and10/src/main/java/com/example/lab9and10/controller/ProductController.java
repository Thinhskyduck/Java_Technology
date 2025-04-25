package com.example.lab9and10.controller;

import com.example.lab9and10.dto.ProductRequest;
import com.example.lab9and10.entity.Product;
import com.example.lab9and10.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET /api/products - Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET /api/products/{id} - Lấy chi tiết sản phẩm
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // Nếu tìm thấy, trả về 200 OK với sản phẩm
                .orElse(ResponseEntity.notFound().build()); // Nếu không tìm thấy, trả về 404 Not Found
    }

    // POST /api/products - Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) { // Bắt lỗi chung, ví dụ DB constraint nếu mã SP trùng
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể tạo sản phẩm: " + e.getMessage(), e);
        }
    }

    // PUT /api/products/{id} - Cập nhật toàn bộ sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request)
                .map(ResponseEntity::ok) // Trả về 200 OK với sản phẩm đã cập nhật
                .orElse(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy ID
    }

    // PATCH /api/products/{id} - Cập nhật một phần sản phẩm
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            // Trả về lỗi nếu request body trống
            return ResponseEntity.badRequest().build();
        }
        return productService.patchProduct(id, updates)
                .map(ResponseEntity::ok) // Trả về 200 OK với sản phẩm đã cập nhật
                .orElse(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy ID
    }

    // DELETE /api/products/{id} - Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content khi xóa thành công
        } else {
            // Có thể do không tìm thấy ID hoặc do logic nghiệp vụ không cho phép xóa (ví dụ: SP đã có trong đơn hàng)
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy hoặc không thể xóa
        }
    }
}