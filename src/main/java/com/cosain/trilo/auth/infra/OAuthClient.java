package com.cosain.trilo.auth.infra;


import com.cosain.trilo.auth.application.dto.OAuthLoginParams;

public interface OAuthClient {
    String getAccessToken(OAuthLoginParams oAuthLoginParams);
    OAuthProfileDto getProfile(String accessToken);
}
