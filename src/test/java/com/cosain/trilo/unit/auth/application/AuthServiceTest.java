package com.cosain.trilo.unit.auth.application;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.JwtProvider;
import com.cosain.trilo.auth.application.OAuthProfileRequestService;
import com.cosain.trilo.auth.application.dto.KakaoLoginParams;
import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.common.exception.NotValidTokenException;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.domain.AuthProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserService userService;
    @Mock
    private OAuthProfileRequestService OAuthProfileRequestService;
    private final String ACCESS_TOKEN = "slkdfjasjeoifjse.siejfoajseifjasolef.sliejfaisjelfsjefsdcv";
    private final String REFRESH_TOKEN = "slkdfjasjeoifjse.siejfoajseifjasolef.dfaesgasegasefasdfase";

    @Test
    void ACCESS_토큰_재발급(){
        Long id = 1L;
        given(jwtProvider.isValidRefreshToken(any())).willReturn(true);
        given(tokenRepository.existsRefreshTokenById(any())).willReturn(true);
        given(jwtProvider.getUserIdFromToken(any())).willReturn(id);
        given(jwtProvider.createAccessToken(anyLong())).willReturn(ACCESS_TOKEN);

        ReIssueAccessTokenResult result = authService.reissueAccessToken(any());
        Assertions.assertThat(result.getAccessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    void 접근토큰_재발급시_재발급_토큰이_유효하지_않다면_에러를_발생시킨다(){

        given(jwtProvider.isValidRefreshToken(any())).willReturn(false);

        assertThatThrownBy(() -> authService.reissueAccessToken(any())).isInstanceOf(NotValidTokenException.class);
    }

    @Test
    void 토큰_정보_생성(){

        // given
        given(jwtProvider.isValidRefreshToken(any())).willReturn(true);

        // when
        RefreshTokenStatusResponse dto = authService.createTokenStatus(any());

        // then
        Assertions.assertThat(dto.isAvailability()).isTrue();
    }

    @Test
    void 토큰_정보_생성시_토큰이_유효하지_않는다면_false를_반환한다(){
        // given
        given(jwtProvider.isValidRefreshToken(any())).willReturn(false);

        // when
        RefreshTokenStatusResponse dto = authService.createTokenStatus(any());

        // then
        Assertions.assertThat(dto.isAvailability()).isFalse();
    }

    @Test
    void 로그아웃(){
        // given
        given(jwtProvider.getTokenRemainExpiry(any())).willReturn(100000L);

        // when
        authService.logout("Bearer accessToken", anyString());

        // then
        then(tokenRepository).should().saveLogoutAccessToken(any());
        then(tokenRepository).should().deleteRefreshTokenById(any());
    }

    @Test
    void 로그인_정상_동작_확인_테스트(){
        // given
        String code = "Authorization Code";
        String redirectUri = "redirect uri";
        String email = "slifjelsijflsiej@nate.com";
        OAuthProfileDto oAuthProfileDto = OAuthProfileDto.builder()
                .email(email)
                .name("홍길동")
                .provider(AuthProvider.KAKAO)
                .profileImageUrl("image_url")
                .build();

        given(userService.createOrUpdate(any(OAuthProfileDto.class))).willReturn(1L);
        given(OAuthProfileRequestService.request(any(OAuthLoginParams.class))).willReturn(oAuthProfileDto);
        given(jwtProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        // when
        LoginResult loginResult = authService.login(KakaoLoginParams.of(code, redirectUri));

        // then
        Assertions.assertThat(loginResult.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        Assertions.assertThat(loginResult.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

}