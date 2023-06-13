package com.cosain.trilo.auth.infra.oauth.naver;

import com.cosain.trilo.auth.infra.NaverClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.oauth.naver.dto.NaverInfoResponse;
import com.cosain.trilo.auth.infra.oauth.naver.dto.NaverTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NaverClientImpl implements NaverClient {
    private final String clientId;
    private final String clinetSecret;
    private final String accessTokenUrl;
    private final String profileUrl;
    private final RestTemplate restTemplate;
    private final String grantType;

    public NaverClientImpl(
            @Value("${oauth2.naver.client-id}") String clientId,
            @Value("${oauth2.naver.client-secret}") String clinetSecret,
            @Value("${oauth2.naver.token_uri}") String accessTokenUrl,
            @Value("${oauth2.naver.user-info-uri}") String profileUrl,
            @Value("${oauth2.naver.grant_type}") String grantType,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.clientId = clientId;
        this.clinetSecret = clinetSecret;
        this.accessTokenUrl = accessTokenUrl;
        this.profileUrl = profileUrl;
        this.grantType = grantType;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String getAccessToken(String code, String state) {

        HttpHeaders headers = makeAccessTokenRequestHeader();
        MultiValueMap<String, String> params = makeAccessTokenRequestParams(code, state);

        HttpEntity<?> request = makeAccessTokenRequest(headers, params);
        NaverTokenResponse naverTokenResponse = restTemplate.postForObject(accessTokenUrl, request, NaverTokenResponse.class);

        return naverTokenResponse.getAccessToken();
    }

    private HttpEntity<MultiValueMap<String, String>> makeAccessTokenRequest(HttpHeaders headers, MultiValueMap<String, String> params) {
        return new HttpEntity<>(params, headers);
    }

    private MultiValueMap<String, String> makeAccessTokenRequestParams(String code, String state) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type",grantType);
        params.add("client_id", clientId);
        params.add("client_secret", clinetSecret);
        params.add("code", code);
        params.add("state", state);

        return params;
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
