package com.example.lab9and10.controller;

import com.example.lab9and10.dto.LoginRequest;
import com.example.lab9and10.dto.LoginResponse;
import com.example.lab9and10.dto.RegisterRequest;
import com.example.lab9and10.entity.User; // Import User entity
import com.example.lab9and10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserService userService;

    // Không cần AuthenticationManager hay JwtTokenProvider nữa

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = userService.registerUser(registerRequest);
            // Có thể trả về thông tin user vừa tạo (không bao gồm password) hoặc chỉ thông báo thành công
            // return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser); // Cẩn thận lộ password nếu chưa bỏ
            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký người dùng thành công!");
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi email đã tồn tại từ service
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            // Bắt các lỗi khác có thể xảy ra
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xảy ra trong quá trình đăng ký.", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> authenticatedUser = userService.authenticate(loginRequest);

        if (authenticatedUser.isPresent()) {
            // Đăng nhập thành công
            User user = authenticatedUser.get();
            // Tạo response đơn giản, không có JWT token
            LoginResponse response = new LoginResponse("Đăng nhập thành công", user.getEmail());
            return ResponseEntity.ok(response);
        } else {
            // Đăng nhập thất bại (sai email hoặc mật khẩu)
            // Trả về lỗi 401 Unauthorized thay vì ném exception để client biết là sai credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Đăng nhập thất bại: Email hoặc mật khẩu không đúng.", null));
            // Hoặc ném exception nếu muốn GlobalExceptionHandler xử lý:
            // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng.");
        }
    }
}