package com.cosain.trilo.auth.infra;


public interface OAuthClient {
    String getAccessToken(String code, String redirectUri);
    OAuthProfileDto getProfile(String accessToken);
}
