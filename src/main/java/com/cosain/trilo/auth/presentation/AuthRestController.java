package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.presentation.dto.AuthResponse;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> reissueAccessToken(@CookieValue(value = "refreshToken", required = true) String refreshToken){
        String accessToken = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(AuthResponse.from(accessToken));
    }

    @GetMapping("/token/refresh-token-info")
    public ResponseEntity<RefreshTokenStatusResponse> checkRefreshTokenStatus(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        return ResponseEntity.ok(authService.createTokenStatus(refreshToken));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization") String authHeaderValue, @CookieValue(value = "refreshToken", required = true) String refreshToken){
        authService.logout(authHeaderValue, refreshToken);
    }

    @GetMapping("/login/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestParam @NotEmpty String code, @RequestParam("redirect_uri") @NotEmpty String redirectUri, @PathVariable String provider, HttpServletResponse response){
        LoginResult loginResult = authService.login(code, provider, redirectUri);
        Cookie cookie = new Cookie("refreshToken", loginResult.getRefreshToken());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);
        return AuthResponse.from(loginResult.getAccessToken());
    }
}
