package com.cosain.trilo.auth.infra.oauth.kakao;

import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.oauth.kakao.dto.KakaoProfileResponse;
import com.cosain.trilo.auth.infra.oauth.kakao.dto.KakaoTokenResponse;
import com.cosain.trilo.user.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoClient implements OAuthClient {

    @Value("${oauth2.kakao.client-id}")
    private String clientId;
    @Value("${oauth2.kakao.token-uri}")
    private String accessTokenUrl;
    @Value("${oauth2.kakao.user-info-uri}")
    private String profileUrl;

    private final RestTemplate restTemplate;
    @Override
    public AuthProvider authProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public String getAccessToken(OAuthLoginParams oAuthLoginParams) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = oAuthLoginParams.getParams();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);

        HttpEntity<?> request = new HttpEntity<>(params, headers);

        KakaoTokenResponse kakaoTokenResponse = restTemplate.postForObject(accessTokenUrl, request, KakaoTokenResponse.class);
        return kakaoTokenResponse.getAccessToken();
    }

    @Override
    public OAuthProfileDto getProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys", "[\"kakao_account.profile\",\"kakao_account.nickname\",\"kakao_account.email\"]");

        HttpEntity<?> request = new HttpEntity<>(params, headers);

        KakaoProfileResponse kakaoProfileResponse = restTemplate.postForObject(profileUrl, request, KakaoProfileResponse.class);
        return OAuthProfileDto.of(kakaoProfileResponse);
    }
}
