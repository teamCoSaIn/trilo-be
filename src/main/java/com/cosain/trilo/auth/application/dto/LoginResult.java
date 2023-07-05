package com.cosain.trilo.auth.application.dto;

import lombok.Getter;

@Getter
public class LoginResult {

    private String accessToken;
    private String refreshToken;

    private LoginResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    public static LoginResult of(String accessToken, String refreshToken) {
        return new LoginResult(accessToken, refreshToken);
    }
}
