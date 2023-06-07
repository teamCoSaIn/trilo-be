package com.cosain.trilo.config.security.handler;

import com.cosain.trilo.auth.domain.RefreshToken;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.config.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.cosain.trilo.config.security.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.cosain.trilo.config.security.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final TokenAnalyzer tokenAnalyzer;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        Long tokenExpiry = tokenAnalyzer.getTokenRemainExpiryFrom(refreshToken);
        tokenRepository.saveRefreshToken(RefreshToken.of(refreshToken, tokenExpiry));

        String targetUrl = determineTargetUrl(request, response, accessToken, refreshToken);
        cookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken){
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

}
