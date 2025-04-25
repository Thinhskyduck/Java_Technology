package com.example.lab9and10.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message; // Trả về thông báo thành công đơn giản
    private String userEmail; // Có thể trả về email hoặc thông tin user cơ bản khác
}