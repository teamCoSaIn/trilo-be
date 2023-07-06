package com.cosain.trilo.auth.presentation.dto;

import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import lombok.Getter;

@Getter
public class AuthResponse {

    private String authType;
    private String accessToken;

    public static AuthResponse from(LoginResult result) {
        return new AuthResponse(result.getAccessToken());
    }

    public static AuthResponse from(ReIssueAccessTokenResult result){
        return new AuthResponse(result.getAccessToken());
    }

    private AuthResponse(String accessToken){
        this.authType = "Bearer";
        this.accessToken = accessToken;
    }
}
