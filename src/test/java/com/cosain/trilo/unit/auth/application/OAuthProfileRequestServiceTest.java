package com.cosain.trilo.unit.auth.application;

import com.cosain.trilo.auth.application.OAuthProfileRequestService;
import com.cosain.trilo.auth.application.dto.KakaoLoginParams;
import com.cosain.trilo.auth.application.dto.NaverLoginParams;
import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.user.domain.AuthProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OAuthProfileRequestServiceTest {
    private OAuthProfileRequestService OAuthProfileRequestService;
    @Mock
    private OAuthClient oAuthClientMock;

    @Test
    public void 카카오_로그인_호출_테스트(){

        given(oAuthClientMock.authProvider()).willReturn(AuthProvider.KAKAO);
        List<OAuthClient> oAuthClientList = Collections.singletonList(oAuthClientMock);
        OAuthProfileRequestService = new OAuthProfileRequestService(oAuthClientList);

        OAuthLoginParams oAuthLoginParams = KakaoLoginParams.of("code", "redirectUri");
        OAuthProfileRequestService.request(oAuthLoginParams);

        verify(oAuthClientMock).getAccessToken(any());
        verify(oAuthClientMock).getProfile(any());
    }

    @Test
    public void 네이버_로그인_호출_테스트(){

        given(oAuthClientMock.authProvider()).willReturn(AuthProvider.NAVER);
        List<OAuthClient> oAuthClientList = Collections.singletonList(oAuthClientMock);
        OAuthProfileRequestService = new OAuthProfileRequestService(oAuthClientList);

        OAuthLoginParams oAuthLoginParams = new NaverLoginParams("code", "state");
        OAuthProfileRequestService.request(oAuthLoginParams);

        verify(oAuthClientMock).getAccessToken(any());
        verify(oAuthClientMock).getProfile(any());
    }


}
