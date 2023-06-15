package com.cosain.trilo.auth.application.dto;

import com.cosain.trilo.user.domain.AuthProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class NaverLoginParams implements OAuthLoginParams{

    private String code;
    private String state;

    public NaverLoginParams(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public static NaverLoginParams of(String code, String state){
        return new NaverLoginParams(code, state);
    }

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> getParams() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("state", state);
        return params;
    }
}
