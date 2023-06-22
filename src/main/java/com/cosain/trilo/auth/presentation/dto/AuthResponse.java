package com.cosain.trilo.auth.presentation.dto;

import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import lombok.Getter;

@Getter
public class AuthResponse {

    private String authType;
    private String accessToken;
    private Long id;

    public static AuthResponse from(LoginResult result) {
        return new AuthResponse(result.getAccessToken(), result.getId());
    }

    public static AuthResponse from(ReIssueAccessTokenResult result){
        return new AuthResponse(result.getAccessToken(), result.getId());
    }

    private AuthResponse(String accessToken, Long id){
        this.authType = "Bearer";
        this.accessToken = accessToken;
        this.id = id;
    }
}
