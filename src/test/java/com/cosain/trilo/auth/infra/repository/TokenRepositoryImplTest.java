package com.cosain.trilo.auth.infra.repository;

import com.cosain.trilo.auth.domain.LogoutAccessToken;
import com.cosain.trilo.auth.domain.RefreshToken;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class TokenRepositoryImplTest {

    @Autowired
    LogoutAccessTokenRepository logoutAccessTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    TokenRepository tokenRepository;


    private static final String TOKEN = "A".repeat(30);

    @BeforeEach
    void setUp(){
        tokenRepository = new TokenRepositoryImpl(logoutAccessTokenRepository, refreshTokenRepository);
        refreshTokenRepository.deleteAll();
        logoutAccessTokenRepository.deleteAll();
    }

    @Test
    void 재발급_토큰_저장(){
        // given
        RefreshToken refreshToken = RefreshToken.of(TOKEN, 1000L);
        // when
        tokenRepository.saveRefreshToken(refreshToken);
        // then
        Assertions.assertThat(tokenRepository.existsRefreshTokenById(TOKEN)).isTrue();
    }

    @Test
    void 재발급_토큰_삭제(){
        // given
        tokenRepository.saveRefreshToken(RefreshToken.of(TOKEN, 1000L));
        // when
        tokenRepository.deleteRefreshTokenById(TOKEN);
        // then
        Assertions.assertThat(tokenRepository.existsRefreshTokenById(TOKEN)).isFalse();
    }

    @Test
    void 로그아웃_접근_토큰_저장(){
        // given
        LogoutAccessToken logoutAccessToken = LogoutAccessToken.of(TOKEN, 1000L);
        // when
        tokenRepository.saveLogoutAccessToken(logoutAccessToken);
        // then
        Assertions.assertThat(tokenRepository.existsLogoutAccessTokenById(TOKEN)).isTrue();
    }

}