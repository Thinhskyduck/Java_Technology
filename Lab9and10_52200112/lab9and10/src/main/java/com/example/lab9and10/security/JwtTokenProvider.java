package com.example.lab9and10.security;

import com.example.lab9and10.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts; // Đảm bảo import đúng
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders; // Cần cho Base64 decode nếu key là Base64
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Import từ io.jsonwebtoken.security
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        // Tạo SecretKey từ chuỗi cấu hình.
        // Nên sử dụng chuỗi Base64 đủ mạnh trong application.properties.
        try {
            // Thử decode Base64 trước, nếu không được thì dùng trực tiếp (ít an toàn hơn)
            byte[] keyBytes;
            try {
                keyBytes = Decoders.BASE64.decode(jwtSecretString);
            } catch (IllegalArgumentException e) {
                logger.warn("CẢNH BÁO: JWT Secret trong cấu hình không phải là Base64 hợp lệ. Sử dụng byte của chuỗi trực tiếp. Điều này KHÔNG được khuyến nghị cho production!");
                keyBytes = jwtSecretString.getBytes();
            }

            if (keyBytes.length < 32) { // Độ dài tối thiểu cho HS256 (nên dùng 64 cho HS512)
                logger.warn("CẢNH BÁO: JWT Secret quá ngắn ({} bytes). Yêu cầu tối thiểu 32 byte cho HS256 hoặc 64 byte cho HS512. Sử dụng key mặc định YẾU KÉM.", keyBytes.length);
                // Tạo key yếu chỉ để chạy test (KHÔNG DÙNG TRONG PRODUCTION)
                String defaultWeakSecret = "ThisIsADefaultDevelopmentSecretKeyForHS512WhichIsVeryWeakAndMustBeChanged";
                this.jwtSecretKey = Keys.hmacShaKeyFor(defaultWeakSecret.getBytes());
            } else {
                this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
                logger.info("Khởi tạo JWT Secret Key thành công.");
            }
        } catch (Exception e) {
            logger.error("Lỗi nghiêm trọng khi khởi tạo JWT secret key: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo JWT secret key", e);
        }
    }

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return generateTokenFromEmail(userPrincipal.getEmail());
    }

    public String generateTokenFromEmail(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // === SỬA ĐỔI CÁCH BUILD TOKEN ===
        return Jwts.builder()
                .subject(email) // Sử dụng .subject() thay vì .setSubject() (mới hơn)
                .issuedAt(now) // Sử dụng .issuedAt() thay vì .setIssuedAt()
                .expiration(expiryDate) // Sử dụng .expiration() thay vì .setExpiration()
                .signWith(jwtSecretKey) // Chỉ cần truyền key, thuật toán được suy ra từ key (HS512 cho SecretKey này)
                .compact();
    }

    // Phương thức parse JWT (vẫn dùng parserBuilder)
    private Claims parseClaims(String token) {
        // --- Đảm bảo dòng này không báo lỗi ---
        return Jwts.parser() // Sử dụng parser() thay vì parserBuilder() trong version mới nhất (0.12.x)
                .verifyWith(jwtSecretKey) // Sử dụng verifyWith() thay vì setSigningKey()
                .build()
                .parseSignedClaims(token) // Sử dụng parseSignedClaims() thay vì parseClaimsJws()
                .getPayload();
    }


    public String getEmailFromJWT(String token) {
        Claims claims = parseClaims(token); // Gọi hàm parse mới
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            // Chỉ cần gọi parseClaims, nếu không ném exception là hợp lệ
            parseClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Chữ ký JWT không hợp lệ: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT không đúng định dạng: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT đã hết hạn: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT không được hỗ trợ: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Lỗi này thường xảy ra nếu token là null hoặc trống trước khi parse
            logger.error("Token JWT không hợp lệ hoặc trống: {}", ex.getMessage());
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra khi parse
            logger.error("Lỗi không xác định khi validate token: {}", e.getMessage());
        }
        return false;
    }
}