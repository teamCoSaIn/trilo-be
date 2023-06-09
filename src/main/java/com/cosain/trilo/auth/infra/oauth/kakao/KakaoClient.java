package com.cosain.trilo.auth.infra.oauth.kakao;

import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.infra.oauth.kakao.dto.KakaoTokenResponse;
import com.cosain.trilo.auth.infra.oauth.kakao.dto.KakaoProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoClient implements OAuthClient {

    private final String clientId;
    private final String accessTokenUrl;
    private final RestTemplate restTemplate;
    private final String profileUrl;
    public KakaoClient(
            @Value("${oauth2.kakao.client-id}") String clientId,
            @Value("${oauth2.kakao.token-uri}") String accessTokenUrl,
            @Value("${oauth2.kakao.user-info-uri}") String profileUrl,
            RestTemplateBuilder restTemplateBuilder
    ){
        this.clientId = clientId;
        this.accessTokenUrl = accessTokenUrl;
        this.profileUrl = profileUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String getAccessToken(String code, String redirectUri) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> kakaoTokenResponseResponseEntity = restTemplate.postForEntity(accessTokenUrl, request, KakaoTokenResponse.class);
        KakaoTokenResponse kakaoTokenResponse = kakaoTokenResponseResponseEntity.getBody();
        return kakaoTokenResponse.getAccessToken();
    }

    @Override
    public OAuthProfileDto getProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys", "[\"kakao_account.profile\",\"kakao_account.nickname\",\"kakao_account.email\"]");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoProfileResponse> profileResponseResponseEntity = restTemplate.postForEntity(profileUrl, request, KakaoProfileResponse.class);
        KakaoProfileResponse kakaoProfileResponse = profileResponseResponseEntity.getBody();

        return OAuthProfileDto.of(kakaoProfileResponse);
    }
}
