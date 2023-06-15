package com.cosain.trilo.auth.infra;


import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.user.domain.AuthProvider;

public interface OAuthClient {

    AuthProvider authProvider();
    String getAccessToken(OAuthLoginParams oAuthLoginParams);
    OAuthProfileDto getProfile(String accessToken);
}
