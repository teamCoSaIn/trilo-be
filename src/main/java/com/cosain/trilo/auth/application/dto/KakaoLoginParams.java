package com.cosain.trilo.auth.application.dto;

import com.cosain.trilo.user.domain.AuthProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoLoginParams implements OAuthLoginParams{

    private String code;

    private String redirectUri;

    private KakaoLoginParams(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }

    public static KakaoLoginParams of(String code, String redirectUri){
        return new KakaoLoginParams(code, redirectUri);
    }

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        return params;
    }
}
