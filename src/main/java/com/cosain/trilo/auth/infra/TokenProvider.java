package com.cosain.trilo.auth.infra;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String createAccessToken(final Authentication authentication);
    String createAccessTokenById(final Long id);
    String createRefreshToken(final Authentication authentication);
    String createRefreshTokenById(final Long id);
}
