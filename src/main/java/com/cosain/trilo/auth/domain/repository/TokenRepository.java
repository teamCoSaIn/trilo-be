package com.cosain.trilo.auth.domain.repository;

import com.cosain.trilo.auth.domain.LogoutAccessToken;
import com.cosain.trilo.auth.domain.RefreshToken;

public interface TokenRepository {

    void saveLogoutAccessToken(LogoutAccessToken logoutAccessToken);
    void saveRefreshToken(RefreshToken refreshToken);
    boolean existsLogoutAccessTokenById(String token);
    boolean existsRefreshTokenById(String token);
    void deleteRefreshTokenById(String token);
}
