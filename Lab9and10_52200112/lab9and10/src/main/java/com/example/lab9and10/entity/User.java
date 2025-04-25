package com.example.lab9and10.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// Thêm import cần thiết cho UserDetails
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections; // Import Collections

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Mật khẩu đã hash

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // --- Implement các phương thức của UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về danh sách quyền hạn (roles). Ví dụ này không dùng roles, trả về rỗng.
        // Nếu có roles, bạn cần tạo GrantedAuthority từ roles của user.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password; // Trả về mật khẩu đã hash
    }

    @Override
    public String getUsername() {
        return email; // Sử dụng email làm username cho Spring Security
    }

    // Các phương thức sau trả về true để đơn giản hóa.
    // Trong ứng dụng thực tế, bạn có thể có logic để khóa tài khoản, hết hạn,...
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}