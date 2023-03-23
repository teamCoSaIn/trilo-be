package com.cosain.trilo.auth.application;

import com.cosain.trilo.auth.domain.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.common.exception.NotExistRefreshTokenException;
import com.cosain.trilo.common.exception.NotValidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final TokenAnalyzer tokenAnalyzer;

    @Transactional
    public String reissueAccessToken(String refreshToken){
        checkIfValidTokenOrThrow(refreshToken);
        checkIfInRedisOrThrow(refreshToken);
        tokenRepository.deleteTokenBy(refreshToken);
        return tokenProvider.createAccessToken(getAuthentication());
    }
    private void checkIfValidTokenOrThrow(String refreshToken){
        if(!tokenAnalyzer.validateToken(refreshToken)){
            throw new NotValidTokenException();
        }
    }
    private void checkIfInRedisOrThrow(String refreshToken){
        if(!tokenRepository.existsById(refreshToken)){
            throw new NotExistRefreshTokenException();
        }
    }
    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
