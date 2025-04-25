package com.example.lab9and10.controller;

import com.example.lab9and10.dto.LoginRequest;
import com.example.lab9and10.dto.LoginResponse;
import com.example.lab9and10.dto.RegisterRequest;
import com.example.lab9and10.entity.User;
import com.example.lab9and10.security.CustomUserDetailsService;
import com.example.lab9and10.security.JwtTokenProvider;
import com.example.lab9and10.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser; // Import cần thiết
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*; // Import Mockito static methods
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity; // Import static method
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Chỉ test AccountController, mock các dependency khác
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc; // Đối tượng để thực hiện các request HTTP giả lập

    @Autowired
    private ObjectMapper objectMapper; // Để chuyển đổi object sang JSON và ngược lại

    @MockBean // Tạo mock bean cho các dependency của AccountController
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

     @MockBean // Không cần mock CustomUserDetailsService vì @WebMvcTest không load nó trừ khi cần cho filter
     private CustomUserDetailsService customUserDetailsService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User registeredUser;
    private Authentication successfulAuthentication;

    // Cài đặt MockMvc để tích hợp Spring Security context
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        // Khởi tạo MockMvc với Spring Security context
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) // Áp dụng cấu hình bảo mật
                .build();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setFirstName("Test");
        validRegisterRequest.setLastName("User");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");

        registeredUser = new User(1L, "test@example.com", "hashedPassword", "Test", "User");

        // Giả lập một đối tượng Authentication thành công
        successfulAuthentication = new UsernamePasswordAuthenticationToken(registeredUser, null, registeredUser.getAuthorities());
    }

    @Test
    @DisplayName("POST /api/account/register - Thành công")
    void registerUser_Success() throws Exception {
        // Giả lập userService.registerUser trả về user đã đăng ký
        given(userService.registerUser(any(RegisterRequest.class))).willReturn(registeredUser);

        // Thực hiện request POST và kiểm tra kết quả
        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isCreated()) // Mong đợi status 201 Created
                .andExpect(content().string("Đăng ký người dùng thành công!")); // Mong đợi body response

        // Kiểm tra xem phương thức service có được gọi không
        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/account/register - Email đã tồn tại")
    void registerUser_EmailExists() throws Exception {
        // Giả lập userService.registerUser ném exception khi email tồn tại
        given(userService.registerUser(any(RegisterRequest.class)))
                .willThrow(new IllegalArgumentException("Địa chỉ email đã được sử dụng."));

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest()) // Mong đợi status 400 Bad Request
                .andExpect(jsonPath("$.message").value("Địa chỉ email đã được sử dụng.")); // Kiểm tra message lỗi

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/account/register - Dữ liệu không hợp lệ")
    void registerUser_InvalidData() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email"); // Email không hợp lệ
        invalidRequest.setPassword("123"); // Password quá ngắn

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // Mong đợi status 400
                .andExpect(jsonPath("$.errors.email").exists()) // Kiểm tra có lỗi cho trường email
                .andExpect(jsonPath("$.errors.password").exists()); // Kiểm tra có lỗi cho trường password

        verify(userService, never()).registerUser(any(RegisterRequest.class)); // Service không được gọi
    }


    @Test
    @DisplayName("POST /api/account/login - Thành công")
    void authenticateUser_Success() throws Exception {
        String fakeToken = "fake-jwt-token";
        // Giả lập authenticationManager trả về đối tượng Authentication thành công
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(successfulAuthentication);
        // Giả lập jwtTokenProvider tạo ra token
        given(jwtTokenProvider.generateToken(any(Authentication.class))).willReturn(fakeToken);

        mockMvc.perform(post("/api/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk()) // Mong đợi status 200 OK
                .andExpect(jsonPath("$.accessToken").value(fakeToken)) // Kiểm tra access token
                .andExpect(jsonPath("$.tokenType").value("Bearer")); // Kiểm tra token type

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    @DisplayName("POST /api/account/login - Sai thông tin đăng nhập")
    void authenticateUser_BadCredentials() throws Exception {
        // Giả lập authenticationManager ném BadCredentialsException
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Sai thông tin đăng nhập"));

        mockMvc.perform(post("/api/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized()) // Mong đợi status 401 Unauthorized
                .andExpect(content().string("Đăng nhập thất bại: Email hoặc mật khẩu không đúng.")); // Kiểm tra message lỗi

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any(Authentication.class)); // Không tạo token nếu login fail
    }
}