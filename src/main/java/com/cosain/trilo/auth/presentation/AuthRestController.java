package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.presentation.dto.AuthResponse;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
