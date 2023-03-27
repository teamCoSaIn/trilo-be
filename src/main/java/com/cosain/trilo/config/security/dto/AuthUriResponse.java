package com.cosain.trilo.config.security.dto;

import lombok.Getter;

@Getter
public class AuthUriResponse {

    private String uri;

    public static AuthUriResponse from(String uri){
        return new AuthUriResponse(uri);
    }

    private AuthUriResponse(String uri){
        this.uri = uri;
    }
}
