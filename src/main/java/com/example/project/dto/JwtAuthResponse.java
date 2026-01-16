package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String message;
    
    public JwtAuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.type = "Bearer";
        this.message = "Login successful";
    }
}