package com.cosain.trilo.auth.infra.oauth.google;

import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.oauth.google.dto.GoogleInfoResponse;
import com.cosain.trilo.auth.infra.oauth.google.dto.GoogleTokenResponse;
import com.cosain.trilo.user.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleClient implements OAuthClient {

    @Value("${oauth2.google.grant_type}")
    private String grantType;
    @Value("${oauth2.google.client-id}")
    private String clientId;
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.token-uri}")
    private String accessTokenUrl;
    @Value("${oauth2.google.user-info-uri}")
    private String profileUrl;

    private final RestTemplate restTemplate;

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public String getAccessToken(OAuthLoginParams oAuthLoginParams) {
        HttpHeaders headers = makeAccessTokenRequestHeader();
        MultiValueMap<String, String> params = oAuthLoginParams.getParams();
        params.add("grant_type",grantType);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);

        HttpEntity<?> request = makeAccessTokenRequest(headers, params);
        GoogleTokenResponse googleTokenResponse = restTemplate.postForObject(accessTokenUrl, request, GoogleTokenResponse.class);

        return googleTokenResponse.getAccessToken();
    }

    private HttpEntity<MultiValueMap<String, String>> makeAccessTokenRequest(HttpHeaders headers, MultiValueMap<String, String> params) {
        return new HttpEntity<>(params, headers);
    }

    private HttpHeaders makeAccessTokenRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    /**
     * the reason why used exchange() : to add headers
     */
    @Override
    public OAuthProfileDto getProfile(String accessToken) {
        HttpHeaders headers = makeUserInfoRequest(accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleInfoResponse> exchange = restTemplate.exchange(profileUrl, HttpMethod.GET, request, GoogleInfoResponse.class);
        GoogleInfoResponse googleInfoResponse = exchange.getBody();
        return OAuthProfileDto.of(googleInfoResponse);
    }



    private HttpHeaders makeUserInfoRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+ accessToken);
        return headers;
    }

}
