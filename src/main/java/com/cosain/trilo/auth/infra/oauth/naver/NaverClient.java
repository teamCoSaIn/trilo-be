package com.cosain.trilo.auth.infra.oauth.naver;

import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.oauth.naver.dto.NaverInfoResponse;
import com.cosain.trilo.auth.infra.oauth.naver.dto.NaverTokenResponse;
import com.cosain.trilo.user.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverClient implements OAuthClient {

    @Value("${oauth2.naver.client-id}")
    private String clientId;
    @Value("${oauth2.naver.client-secret}")
    private String clinetSecret;
    @Value("${oauth2.naver.token_uri}")
    private String accessTokenUrl;
    @Value("${oauth2.naver.user-info-uri}")
    private String profileUrl;
    @Value("${oauth2.naver.grant_type}")
    private String grantType;
    private final RestTemplate restTemplate;

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.NAVER;
    }

    @Override
    public String getAccessToken(OAuthLoginParams oAuthLoginParams) {

        HttpHeaders headers = makeAccessTokenRequestHeader();
        MultiValueMap<String, String> params = oAuthLoginParams.getParams();
        params.add("grant_type",grantType);
        params.add("client_id", clientId);
        params.add("client_secret", clinetSecret);

        HttpEntity<?> request = makeAccessTokenRequest(headers, params);

        NaverTokenResponse naverTokenResponse = restTemplate.postForObject(accessTokenUrl, request, NaverTokenResponse.class);

        return naverTokenResponse.getAccessToken();
    }

    private HttpEntity<MultiValueMap<String, String>> makeAccessTokenRequest(HttpHeaders headers, MultiValueMap<String, String> params) {
        return new HttpEntity<>(params, headers);
    }

    private HttpHeaders makeAccessTokenRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Override
    public OAuthProfileDto getProfile(String accessToken) {

        HttpHeaders headers = makeUserInfoRequest(accessToken);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(params, headers);
        NaverInfoResponse naverInfoResponse = restTemplate.postForObject(profileUrl, request, NaverInfoResponse.class);
        return OAuthProfileDto.of(naverInfoResponse);
    }

    private HttpHeaders makeUserInfoRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);
        return headers;
    }
}
