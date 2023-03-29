package com.cosain.trilo.config.security.handler;

import com.cosain.trilo.auth.domain.RefreshToken;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.repository.RefreshTokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.auth.presentation.dto.AuthResponse;
import com.cosain.trilo.config.security.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final TokenAnalyzer tokenAnalyzer;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        Long tokenExpiry = tokenAnalyzer.getTokenExpiryFrom(refreshToken);
        tokenRepository.saveRefreshToken(RefreshToken.of(refreshToken, tokenExpiry));

        CookieUtil.addAuthCookie(response, refreshToken, tokenExpiry);
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(response.getWriter(), AuthResponse.from(accessToken));
    }

}
