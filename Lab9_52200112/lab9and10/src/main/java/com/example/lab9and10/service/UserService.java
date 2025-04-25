package com.example.lab9and10.service;

import com.example.lab9and10.dto.LoginRequest;
import com.example.lab9and10.dto.RegisterRequest;
import com.example.lab9and10.entity.User;
import com.example.lab9and10.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Vẫn cần để mã hóa và kiểm tra mật khẩu

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Địa chỉ email đã được sử dụng.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true) // Chỉ đọc, không thay đổi dữ liệu
    public Optional<User> authenticate(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Kiểm tra mật khẩu người dùng nhập với mật khẩu đã mã hóa trong DB
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return userOptional; // Mật khẩu khớp, trả về thông tin user
            }
        }
        // Không tìm thấy user hoặc mật khẩu không khớp
        return Optional.empty();
    }
}