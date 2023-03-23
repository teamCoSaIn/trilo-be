package com.cosain.trilo.auth.presentation.dto;

import lombok.Getter;

@Getter
public class AuthResponse {

    private String authType;
    private String accessToken;

    public static AuthResponse from(String accessToken) {
        return new AuthResponse(accessToken);
    }

    private AuthResponse(String accessToken){
        this.authType = "Bearer";
        this.accessToken = accessToken;
    }
}
