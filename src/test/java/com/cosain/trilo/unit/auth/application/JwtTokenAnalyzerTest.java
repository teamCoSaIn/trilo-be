package com.cosain.trilo.unit.auth.application;

import com.cosain.trilo.auth.infra.jwt.JwtTokenAnalyzer;
import com.cosain.trilo.auth.infra.jwt.JwtTokenProvider;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.support.auth.AuthHelper;
import com.cosain.trilo.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;

class JwtTokenAnalyzerTest {

    private static final String SECRET_KEY = "K".repeat(32);
    private TokenAnalyzer tokenAnalyzer;

    @BeforeEach
    void setUp(){
        tokenAnalyzer = new JwtTokenAnalyzer(SECRET_KEY);
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
        User user = KAKAO_MEMBER.create();
        String accessToken = createAccessToken(user, 100000L, 1000000L, SECRET_KEY);

        // when
        Long userId = tokenAnalyzer.getUserIdFromToken(accessToken);

        // then
        Assertions.assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    void 토큰_유효성_검사_성공시_TRUE를_반환한다(){
        // given
        User user = KAKAO_MEMBER.create();
        String accessToken = createAccessToken(user, 100000L, 1000000L, SECRET_KEY);

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void 토큰_유효성_검사시_secretKey가_다르면_FALSE를_반환한다(){
        // given
        User user = KAKAO_MEMBER.create();
        String accessToken = createAccessToken(user,100000L, 100000L, "S".repeat(32));

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 만료된_토큰_유효성_검사시_토큰_만료기간이_지났다면_FALSE를_반환한다(){
        // given
        User user = KAKAO_MEMBER.create();
        String accessToken = createAccessToken(user, 0L, 0L, SECRET_KEY);

        // when
        boolean result = tokenAnalyzer.validateToken(accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_유효기간_추출하기(){
        // given
        User user = KAKAO_MEMBER.create();
        String accessToken = createAccessToken(user, 100000L, 1000000L, SECRET_KEY);

        // when
        Long tokenExpiryFrom = tokenAnalyzer.getTokenRemainExpiryFrom(accessToken);

        // then
        Assertions.assertThat(tokenExpiryFrom).isNotNull();
    }

    private String createAccessToken(User user, Long accessTokenExpiry, Long refreshTokenExpiry, String secretKey){
        TokenProvider tokenProvider = new JwtTokenProvider(accessTokenExpiry, refreshTokenExpiry, secretKey);
        Authentication authentication = AuthHelper.createAuthentication(user);
        return tokenProvider.createAccessToken(authentication);
    }

}