package com.cosain.trilo.auth.application.dto;

import lombok.Getter;

@Getter
public class LoginResult {

    private String accessToken;
    private String refreshToken;
    private Long id;

    private LoginResult(String accessToken, String refreshToken, Long id) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
    }
    public static LoginResult of(String accessToken, String refreshToken, Long id) {
        return new LoginResult(accessToken, refreshToken, id);
    }
}
