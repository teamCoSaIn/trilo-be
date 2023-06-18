package com.cosain.trilo.auth.application.dto;

import com.cosain.trilo.user.domain.AuthProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class GoogleLoginParams implements OAuthLoginParams{

    private String code;
    private String redirectUri;

    private GoogleLoginParams(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }

    public static GoogleLoginParams of(String code, String redirectUri){
        return new GoogleLoginParams(code, redirectUri);
    }
    @Override
    public AuthProvider authProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code.replace("%2F", "/"));
        params.add("redirect_uri", redirectUri);;
        return params;
    }
}
