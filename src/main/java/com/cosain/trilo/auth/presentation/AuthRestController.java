package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.dto.*;
import com.cosain.trilo.auth.presentation.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse reissueAccessToken(@CookieValue(value = "refreshToken", required = true) String refreshToken){
        ReIssueAccessTokenResult result = authService.reissueAccessToken(refreshToken);
        return AuthResponse.from(result);
    }

    @GetMapping("/token/refresh-token-info")
    @ResponseStatus(HttpStatus.OK)
    public RefreshTokenStatusResponse checkRefreshTokenStatus(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        RefreshTokenStatusResponse refreshTokenStatusResponse = authService.createTokenStatus(refreshToken);
        return refreshTokenStatusResponse;
    }

    @PostMapping("/logout")
    @Login
    public void logout(@RequestHeader(value = "Authorization") String authHeaderValue, @CookieValue(value = "refreshToken", required = true) String refreshToken){
        authService.logout(authHeaderValue, refreshToken);
    }

    @PostMapping("/login/kakao")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody KakaoOAuthLoginRequest kakaoOAuthLoginRequest, HttpServletResponse response){
        LoginResult loginResult = authService.login(KakaoLoginParams.of(kakaoOAuthLoginRequest.getCode(), kakaoOAuthLoginRequest.getRedirect_uri()));
        Cookie cookie = makeRefreshTokenCookie(loginResult.getRefreshToken());
        response.addCookie(cookie);
        return  AuthResponse.from(loginResult);
    }

    @PostMapping("/login/naver")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody NaverOAuthLoginRequest naverOAuthLoginRequest, HttpServletResponse response){
        LoginResult loginResult = authService.login(NaverLoginParams.of(naverOAuthLoginRequest.getCode(), naverOAuthLoginRequest.getState()));
        Cookie cookie = makeRefreshTokenCookie(loginResult.getRefreshToken());
        response.addCookie(cookie);
        return AuthResponse.from(loginResult);
    }

    @PostMapping("/login/google")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody GoogleOAuthLoginRequest googleOAuthLoginRequest, HttpServletResponse response){
        LoginResult loginResult = authService.login(GoogleLoginParams.of(googleOAuthLoginRequest.getCode(), googleOAuthLoginRequest.getRedirect_uri()));
        Cookie cookie = makeRefreshTokenCookie(loginResult.getRefreshToken());
        response.addCookie(cookie);
        return AuthResponse.from(loginResult);
    }

    private Cookie makeRefreshTokenCookie(String refreshTokenStr){
        Cookie cookie = new Cookie("refreshToken", refreshTokenStr);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        return cookie;
    }
}
