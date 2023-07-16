package com.cosain.trilo.unit.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.auth.infra.jwt.JwtTokenAnalyzer;
import com.cosain.trilo.auth.infra.jwt.JwtTokenProvider;
import com.cosain.trilo.auth.presentation.AuthTokenExtractor;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenAnalyzerTest {

    private final String SECRET_KEY = "K".repeat(32);
    private final int ACCESS_TOKEN_EXPIRY = 1000;
    private final int REFRESH_TOKEN_EXPIRY = 10000;
    private TokenAnalyzer tokenAnalyzer;
    private TokenProvider tokenProvider;
    private AuthTokenExtractor authTokenExtractor;

    @BeforeEach
    void setUp(){
        tokenProvider =  new JwtTokenProvider(ACCESS_TOKEN_EXPIRY, REFRESH_TOKEN_EXPIRY, SECRET_KEY);
        tokenAnalyzer = new JwtTokenAnalyzer(authTokenExtractor, SECRET_KEY);
    }

    @Test
    void 토큰이_32바이트를_만족하지_않는다면_FALSE를_반환한다(){
        // given
        String accessToken = "aaa";

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_ID_추출하기(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = tokenProvider.createAccessTokenById(user.getId());

        // when
        Long userId = tokenAnalyzer.getUserIdFromToken(accessToken);

        // then
        Assertions.assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    void 토큰_유효성_검사_성공시_TRUE를_반환한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = tokenProvider.createAccessTokenById(user.getId());

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void 토큰_유효성_검사시_secretKey가_다르면_FALSE를_반환한다(){
        // given
        tokenProvider = new JwtTokenProvider(ACCESS_TOKEN_EXPIRY, REFRESH_TOKEN_EXPIRY, "S".repeat(32));
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = tokenProvider.createAccessTokenById(user.getId());

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 만료된_토큰_유효성_검사시_토큰_만료기간이_지났다면_FALSE를_반환한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        tokenProvider = new JwtTokenProvider(0, 0, SECRET_KEY);
        String accessToken = tokenProvider.createAccessTokenById(user.getId());

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_유효기간_추출하기(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = tokenProvider.createAccessTokenById(user.getId());

        // when
        Long tokenExpiryFrom = tokenAnalyzer.getTokenRemainExpiryFrom(accessToken);

        // then
        Assertions.assertThat(tokenExpiryFrom).isNotNull();
    }


}