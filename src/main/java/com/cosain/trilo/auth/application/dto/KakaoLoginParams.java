package com.cosain.trilo.auth.application.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoLoginParams implements OAuthLoginParams{

    private String code;

    public KakaoLoginParams(String code) {
        this.code = code;
    }

    public static KakaoLoginParams of(String code){
        return new KakaoLoginParams(code);
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        return params;
    }
}
