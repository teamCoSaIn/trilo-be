package com.cosain.trilo.auth.application;

import com.cosain.trilo.auth.domain.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.common.exception.NotExistRefreshTokenException;
import com.cosain.trilo.common.exception.NotValidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final TokenAnalyzer tokenAnalyzer;

    @Transactional
    public String reissueAccessToken(String refreshToken){
        checkIfValidTokenOrThrow(refreshToken);
        checkTokenExistenceOrThrow(refreshToken);
        String email = tokenAnalyzer.getEmailFromToken(refreshToken);
        tokenRepository.deleteTokenBy(refreshToken);
        return tokenProvider.createAccessToken(email);
    }
    private void checkIfValidTokenOrThrow(String refreshToken){
        if(!tokenAnalyzer.validateToken(refreshToken)){
            throw new NotValidTokenException();
        }
    }
    private void checkTokenExistenceOrThrow(String refreshToken){
        if(!tokenRepository.existsById(refreshToken)){
            throw new NotExistRefreshTokenException();
        }
    }

    public RefreshTokenStatusResponse createTokenStatus(String token) {
        boolean availability = tokenAnalyzer.validateToken(token);
        if(!availability) throw new NotValidTokenException();
        LocalDateTime expiryDateTime = tokenAnalyzer.getTokenExpiryDateTime(token);
        return RefreshTokenStatusResponse.of(availability,expiryDateTime);
    }

}
