package com.example.lab9and10.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    // Phương thức này được gọi khi user chưa xác thực cố gắng truy cập tài nguyên bảo mật
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        logger.error("Lỗi xác thực chưa được cấp phép. Message - {}", e.getMessage());
        // Trả về lỗi 401 Unauthorized
        // Bạn có thể tùy chỉnh response body nếu muốn
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lỗi: Chưa được cấp phép");
    }
}