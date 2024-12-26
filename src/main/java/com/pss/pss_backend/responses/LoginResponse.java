package com.pss.pss_backend.responses;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private long expiresIn;

    private String role;

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setRole(String role) {
        this.role = role;
        return this;
    }
}