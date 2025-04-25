package com.example.lab9and10.service;

import com.example.lab9and10.dto.ProductRequest;
import com.example.lab9and10.entity.Product;
import com.example.lab9and10.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;


import java.lang.reflect.Field;
import java.math.BigDecimal; // Import BigDecimal
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        // Có thể thêm kiểm tra trùng mã sản phẩm ở đây nếu cần
        // if (productRepository.existsByCode(request.getCode())) { ... }

        Product product = new Product();
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setIllustration(request.getIllustration());
        product.setDescription(request.getDescription());
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Có thể thêm kiểm tra nếu mã sản phẩm mới bị trùng với sản phẩm khác
                    existingProduct.setCode(request.getCode());
                    existingProduct.setName(request.getName());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setIllustration(request.getIllustration());
                    existingProduct.setDescription(request.getDescription());
                    return productRepository.save(existingProduct);
                });
    }

    @Transactional
    public Optional<Product> patchProduct(Long id, Map<String, Object> updates) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updates.forEach((key, value) -> {
                        try {
                            Field field = ReflectionUtils.findField(Product.class, key);
                            if (field != null) {
                                field.setAccessible(true);
                                // Xử lý kiểu dữ liệu cẩn thận hơn
                                if (field.getType().equals(BigDecimal.class) && value != null) {
                                    // Chuyển đổi từ Number hoặc String sang BigDecimal
                                    if (value instanceof Number) {
                                        ReflectionUtils.setField(field, existingProduct, new BigDecimal(value.toString()));
                                    } else if (value instanceof String) {
                                        try {
                                            ReflectionUtils.setField(field, existingProduct, new BigDecimal((String) value));
                                        } catch (NumberFormatException e) {
                                            System.err.println("Lỗi định dạng số cho trường " + key + ": " + value);
                                            // Có thể bỏ qua hoặc ném ngoại lệ tùy chỉnh
                                        }
                                    }
                                } else if (field.getType().equals(String.class)) {
                                    ReflectionUtils.setField(field, existingProduct, value); // Cho phép null cho String
                                } else {
                                    System.out.println("Bỏ qua cập nhật cho trường: " + key + " - Kiểu dữ liệu không khớp hoặc chưa xử lý: " + (value != null ? value.getClass().getName() : "null"));
                                }
                            } else {
                                System.out.println("Bỏ qua cập nhật cho trường: " + key + " - Không tìm thấy trường");
                            }
                        } catch (Exception e) {
                            System.err.println("Lỗi khi cập nhật trường " + key + ": " + e.getMessage());
                            // Ghi log hoặc ném ngoại lệ cụ thể
                        }
                    });
                    return productRepository.save(existingProduct);
                });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            // Cần kiểm tra xem sản phẩm có trong đơn hàng nào không trước khi xóa (logic nghiệp vụ)
            // Ví dụ: if (!orderRepository.existsByProductsId(id)) { ... }
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}