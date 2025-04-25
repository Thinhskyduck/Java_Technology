package com.example.lab9and10.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users") // Tên bảng trong DB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Sẽ lưu trữ mật khẩu đã được hash

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // Bỏ implements UserDetails vì không dùng Spring Security
}