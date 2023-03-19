package com.cosain.trilo.auth.infra;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String createAccessToken(final Authentication authentication);
    String createRefreshToken(final Authentication authentication);
}
