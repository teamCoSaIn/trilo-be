package com.cosain.trilo.unit.auth.application;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.OAuthProfileRequestService;
import com.cosain.trilo.auth.application.dto.KakaoLoginParams;
import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.common.exception.NotExistRefreshTokenException;
import com.cosain.trilo.common.exception.NotValidTokenException;
import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    private TokenAnalyzer tokenAnalyzer;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OAuthProfileRequestService OAuthProfileRequestService;
    private final String ACCESS_TOKEN = "slkdfjasjeoifjse.siejfoajseifjasolef.sliejfaisjelfsjefsdcv";
    private final String REFRESH_TOKEN = "slkdfjasjeoifjse.siejfoajseifjasolef.dfaesgasegasefasdfase";

    @Test
    void ACCESS_토큰_재발급(){
        Long id = 1L;
        given(tokenAnalyzer.validateToken(any())).willReturn(true);
        given(tokenRepository.existsRefreshTokenById(any())).willReturn(true);
        given(tokenAnalyzer.getUserIdFromToken(any())).willReturn(id);
        given(tokenProvider.createAccessTokenById(anyLong())).willReturn(ACCESS_TOKEN);

        ReIssueAccessTokenResult result = authService.reissueAccessToken(any());
        Assertions.assertThat(result.getUserId()).isEqualTo(id);
        Assertions.assertThat(result.getAccessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    void 접근토큰_재발급시_재발급_토큰이_유효하지_않다면_에러를_발생시킨다(){
        given(tokenAnalyzer.validateToken(any())).willReturn(false);

        assertThatThrownBy(() -> authService.reissueAccessToken(any())).isInstanceOf(NotValidTokenException.class);
    }

    @Test
    void 접근토큰_재발급시_토큰_저장소에_재발급_토큰이_존재하지_않는다면_에러를_발생시킨다(){
        given(tokenAnalyzer.validateToken(any())).willReturn(true);
        given(tokenRepository.existsRefreshTokenById(any())).willReturn(false);

        assertThatThrownBy(() -> authService.reissueAccessToken(any())).isInstanceOf(NotExistRefreshTokenException.class);
    }

    @Test
    void 토큰_정보_생성(){

        // given
        given(tokenAnalyzer.validateToken(any())).willReturn(true);

        // when
        RefreshTokenStatusResponse dto = authService.createTokenStatus(any());

        // then
        Assertions.assertThat(dto.isAvailability()).isTrue();
    }

    @Test
    void 토큰_정보_생성시_토큰이_유효하지_않는다면_false를_반환한다(){
        // given
        given(tokenAnalyzer.validateToken(any())).willReturn(false);

        // when
        RefreshTokenStatusResponse dto = authService.createTokenStatus(any());

        // then
        Assertions.assertThat(dto.isAvailability()).isFalse();
    }

    @Test
    void 로그아웃(){
        // given
        given(tokenAnalyzer.getTokenRemainExpiryFrom(any())).willReturn(100000L);

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
        given(userRepository.findByEmail(any())).willReturn(Optional.ofNullable(User.from(oAuthProfileDto)));
        given(OAuthProfileRequestService.request(any(OAuthLoginParams.class))).willReturn(oAuthProfileDto);
        given(tokenProvider.createAccessTokenById(any())).willReturn(ACCESS_TOKEN);
        given(tokenProvider.createRefreshTokenById(any())).willReturn(REFRESH_TOKEN);

        // when
        LoginResult loginResult = authService.login(KakaoLoginParams.of(code, redirectUri));

        // then
        Assertions.assertThat(loginResult.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        Assertions.assertThat(loginResult.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

}