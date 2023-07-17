package com.cosain.trilo.unit.auth.application;

import com.cosain.trilo.auth.application.JwtProvider;
import com.cosain.trilo.auth.infra.token.JwtProviderImpl;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private final String SECRET_KEY = "K".repeat(32);
    private final int ACCESS_TOKEN_EXPIRY = 1000;
    private final int REFRESH_TOKEN_EXPIRY = 10000;
    private final String TOKEN_TYPE = "Bearer";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp(){
        jwtProvider = new JwtProviderImpl(ACCESS_TOKEN_EXPIRY, REFRESH_TOKEN_EXPIRY, SECRET_KEY);
    }

    @Test
    void 토큰이_32바이트를_만족하지_않는다면_FALSE를_반환한다(){
        // given
        String accessToken = "aaa";

        // when
        boolean result = jwtProvider.isValidAccessToken(TOKEN_TYPE+" "+accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_ID_추출하기(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // when
        Long userId = jwtProvider.getUserIdFromToken(accessToken);

        // then
        Assertions.assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    void 토큰_유효성_검사_성공시_TRUE를_반환한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // when
        boolean result = jwtProvider.isValidAccessToken(TOKEN_TYPE +" "+accessToken);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void 만료된_토큰_유효성_검사시_토큰_만료기간이_지났다면_FALSE를_반환한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        jwtProvider = new JwtProviderImpl(0, 0, SECRET_KEY);
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // when
        boolean result = jwtProvider.isValidAccessToken(TOKEN_TYPE +" "+ accessToken);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_유효기간_추출하기(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // when
        Long tokenExpiryFrom = jwtProvider.getTokenRemainExpiry(accessToken);

        // then
        Assertions.assertThat(tokenExpiryFrom).isNotNull();
    }

    @Test
    void 사용자_ID로_접근_토큰을_생성한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        // when
        String accessToken = jwtProvider.createAccessToken(user.getId());
        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    void 사용자_ID로_재발급_토큰을_생성한다(){
        // given
        User user = UserFixture.kakaoUser_Id(1L);
        // when
        String accessToken = jwtProvider.createRefreshToken(user.getId());
        // then
        assertThat(accessToken).isNotNull();
    }


}