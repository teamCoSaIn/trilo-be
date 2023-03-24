package com.cosain.trilo.auth.infra;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String createAccessToken(final Authentication authentication);
    String createAccessToken(final String email);
    String createRefreshToken(final Authentication authentication);
}
