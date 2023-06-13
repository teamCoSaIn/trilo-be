package com.cosain.trilo.auth.application.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoLoginParams implements OAuthLoginParams{

    private String code;
    private String redirectUrl;

    public KakaoLoginParams(String code, String redirectUrl) {
        this.code = code;
        this.redirectUrl = redirectUrl;
    }

    public static KakaoLoginParams of(String code, String redirectUrl){
        return new KakaoLoginParams(code, redirectUrl);
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUrl);
        return params;
    }
}
