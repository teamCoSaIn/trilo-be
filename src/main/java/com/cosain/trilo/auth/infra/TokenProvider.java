package com.cosain.trilo.auth.infra;


public interface TokenProvider {
    String createAccessTokenById(final Long id);
    String createRefreshTokenById(final Long id);
}
