package com.cosain.trilo.auth.application;

import com.cosain.trilo.auth.domain.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.common.exception.NotExistRefreshTokenException;
import com.cosain.trilo.common.exception.NotValidTokenException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

    private final String ACCESS_TOKEN = "slkdfjasjeoifjse.siejfoajseifjasolef.sliejfaisjelfsjefsdcv";

    @Test
    void ACCESS_토큰_재발급(){
        given(tokenAnalyzer.validateToken(any())).willReturn(true);
        given(tokenProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(tokenRepository.existsById(any())).willReturn(true);

        String accessToken = authService.reissueAccessToken(any());
        Assertions.assertThat(accessToken).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    void 접근토큰_재발급시_재발급_토큰이_유효하지_않다면_에러를_발생시킨다(){
        given(tokenAnalyzer.validateToken(any())).willReturn(false);

        Assertions.assertThatThrownBy(() -> authService.reissueAccessToken(any())).isInstanceOf(NotValidTokenException.class);
    }

    @Test
    void 접근토큰_재발급시_토큰_저장소에_재발급_토큰이_존재하지_않는다면_에러를_발생시킨다(){
        given(tokenAnalyzer.validateToken(any())).willReturn(true);
        given(tokenRepository.existsById(any())).willReturn(false);

        Assertions.assertThatThrownBy(() -> authService.reissueAccessToken(any())).isInstanceOf(NotExistRefreshTokenException.class);
    }
}