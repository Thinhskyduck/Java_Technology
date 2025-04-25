package com.example.lab9and10.dto;

import lombok.Getter;
import lombok.Setter; // Thêm Setter nếu cần

@Getter
@Setter // Thêm Setter nếu cần
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Chuẩn prefix cho JWT

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    // Bỏ các trường không cần thiết khác như message, userEmail
}