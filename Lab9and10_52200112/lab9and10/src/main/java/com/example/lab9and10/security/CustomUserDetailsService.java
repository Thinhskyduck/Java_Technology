package com.example.lab9and10.security;

import com.example.lab9and10.entity.User;
import com.example.lab9and10.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Chỉ đọc dữ liệu
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Username của chúng ta là email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email)
                );
        // User entity đã implement UserDetails, nên có thể trả về trực tiếp
        return user;
    }

    // Phương thức này được sử dụng bởi JwtAuthenticationFilter để tải user theo ID nếu cần
    // (Trong ví dụ này, chúng ta tải bằng email là đủ)
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Không tìm thấy người dùng với ID : " + id)
        );
    }
}