package com.example.lab9and10.config;

import com.example.lab9and10.security.CustomUserDetailsService;
import com.example.lab9and10.security.JwtAuthenticationEntryPoint;
import com.example.lab9and10.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder; // Giữ lại import này
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Bật @PreAuthorize nếu muốn dùng
public class SecurityConfig {

    // Giữ lại PasswordEncoder Bean từ AppConfig nếu có, hoặc định nghĩa lại ở đây
    // @Autowired
    // private PasswordEncoder passwordEncoder; // Không cần inject trực tiếp ở đây

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Sẽ tạo ở bước sau

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler; // Sẽ tạo ở bước sau

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() { // Sẽ tạo ở bước sau
        return new JwtAuthenticationFilter();
    }

    // Bean AuthenticationManager cần thiết cho việc xác thực username/password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // Kích hoạt CORS sử dụng bean CorsConfigurationSource bên dưới
                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF cho API stateless
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Xử lý lỗi khi truy cập tài nguyên cần xác thực mà chưa đăng nhập
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Không sử dụng session phía server
                )
                .authorizeHttpRequests(authz -> authz
                        // Cho phép các endpoint public
                        .requestMatchers("/api/account/register", "/api/account/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Cho phép truy cập Swagger UI và API docs
                        // Cho phép GET sản phẩm công khai
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                        // Yêu cầu xác thực cho các endpoint được tô đỏ trong đề bài
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                        .requestMatchers("/api/orders", "/api/orders/**").authenticated() // Tất cả endpoint order yêu cầu xác thực
                        // Bất kỳ request nào khác chưa được khai báo ở trên đều yêu cầu xác thực
                        .anyRequest().authenticated()
                );

        // Thêm bộ lọc JWT của chúng ta vào trước bộ lọc UsernamePasswordAuthenticationFilter mặc định
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Cấu hình CORS Bean
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép tất cả origin (thay đổi thành domain cụ thể trong production)
        configuration.setAllowedOrigins(List.of("*"));
        // Cho phép các method HTTP cần thiết
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cho phép tất cả header (bao gồm cả Authorization)
        configuration.setAllowedHeaders(List.of("*"));
        // configuration.setAllowCredentials(true); // Có thể cần nếu dùng cookie/session
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả đường dẫn
        return source;
    }
}