package com.cosain.trilo.auth.infra.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("code")
    private String code;


    private KakaoTokenRequest(String clientId, String code, String redirectUri) {
        this.clientId = clientId;
        this.code = code;
        this.grantType = "authorization_code";
        this.redirectUri = redirectUri;
    }

    public static KakaoTokenRequest of(String clientId,  String code, String redirectUri){
        return new KakaoTokenRequest(clientId, code, redirectUri);
    }
}
