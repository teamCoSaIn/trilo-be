package com.cosain.trilo.auth.application;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.assertj.core.api.Assertions.*;

import com.cosain.trilo.auth.infra.jwt.JwtTokenProvider;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.support.auth.AuthHelper;
import com.cosain.trilo.user.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp(){
        String secretKey = "K".repeat(32);
        int accessTokenExpiry = 100;
        int refreshTokenExpiry = 1000;
        tokenProvider =  new JwtTokenProvider(accessTokenExpiry, refreshTokenExpiry,secretKey);
    }

    @Test
    void 인증_객체로_접근_토큰을_생성한다(){
        // given
        User user = KAKAO_MEMBER.create();
        // when
        String accessToken = tokenProvider.createAccessToken(AuthHelper.createAuthentication(user));
        // then
        assertThat(accessToken).isNotNull();

    }

    @Test
    void 이메일로_접근_토큰을_생성한다(){
        // given
        User user = KAKAO_MEMBER.create();
        // when
        String accessToken = tokenProvider.createAccessToken(user.getEmail());
        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    void 재발급_토큰을_생성한다(){
        // given
        User user = KAKAO_MEMBER.create();
        // when
        String refreshToken = tokenProvider.createRefreshToken(AuthHelper.createAuthentication(user));
        // then
        assertThat(refreshToken).isNotNull();
    }
}