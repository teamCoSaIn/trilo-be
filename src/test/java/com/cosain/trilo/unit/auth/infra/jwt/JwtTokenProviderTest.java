package com.cosain.trilo.unit.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.auth.infra.jwt.JwtTokenProvider;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp(){
        String secretKey = "K".repeat(32);
        int accessTokenExpiry = 100;
        int refreshTokenExpiry = 1000;
        tokenProvider =  new JwtTokenProvider(accessTokenExpiry, refreshTokenExpiry, secretKey);
    }

    @Test
    void 사용자_ID로_접근_토큰을_생성한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        // when
        String accessToken = tokenProvider.createAccessTokenById(user.getId());
        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    void 사용자_ID로_재발급_토큰을_생성한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        // when
        String accessToken = tokenProvider.createRefreshTokenById(user.getId());
        // then
        assertThat(accessToken).isNotNull();
    }

}
