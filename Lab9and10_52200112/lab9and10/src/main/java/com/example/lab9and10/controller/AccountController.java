package com.example.lab9and10.controller;

import com.example.lab9and10.dto.LoginRequest;
import com.example.lab9and10.dto.LoginResponse; // Sửa đổi LoginResponse
import com.example.lab9and10.dto.RegisterRequest;
import com.example.lab9and10.entity.User;
import com.example.lab9and10.security.JwtTokenProvider; // Import JwtTokenProvider
import com.example.lab9and10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Import các lớp cần thiết của Spring Security
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Bỏ import Optional nếu không dùng trong login nữa

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager; // Inject AuthenticationManager

    @Autowired
    private JwtTokenProvider tokenProvider; // Inject JwtTokenProvider

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Giữ nguyên logic registerUser
        try {
            User registeredUser = userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký người dùng thành công!");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xảy ra trong quá trình đăng ký.", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Thực hiện xác thực bằng AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), // Dùng email làm username
                            loginRequest.getPassword()
                    )
            );

            // Nếu xác thực thành công, thiết lập Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Trả về token cho client
            return ResponseEntity.ok(new LoginResponse(jwt)); // Sử dụng LoginResponse mới

        } catch (AuthenticationException e) {
            // Xử lý nếu xác thực thất bại (sai email/password)
            logger.error("Xác thực thất bại cho user {}: {}", loginRequest.getEmail(), e.getMessage());
            // Trả về lỗi 401 hoặc thông báo lỗi cụ thể hơn
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Đăng nhập thất bại: Email hoặc mật khẩu không đúng.");
            // Hoặc throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng.", e);
        }
    }
    // Thêm logger nếu muốn log lỗi xác thực
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AccountController.class);
}