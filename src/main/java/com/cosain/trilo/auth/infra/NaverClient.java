package com.cosain.trilo.auth.infra;

public interface NaverClient {
    String getAccessToken(String code, String state);

    OAuthProfileDto getProfile(String accessToken);
}
