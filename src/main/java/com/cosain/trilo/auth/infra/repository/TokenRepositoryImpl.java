package com.cosain.trilo.auth.infra.repository;

import com.cosain.trilo.auth.domain.LogoutAccessToken;
import com.cosain.trilo.auth.domain.RefreshToken;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository{

    private final LogoutAccessTokenRepository logoutAccessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void saveLogoutAccessToken(LogoutAccessToken logoutAccessToken) {
        logoutAccessTokenRepository.save(logoutAccessToken);
    }

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean existsLogoutAccessTokenById(String logoutAccessToken) {
        return logoutAccessTokenRepository.existsById(logoutAccessToken);
    }

    @Override
    public boolean existsRefreshTokenById(String refreshToken) {
        return refreshTokenRepository.existsById(refreshToken);
    }

    @Override
    public void deleteRefreshTokenById(String refreshToken){
        refreshTokenRepository.deleteById(refreshToken);
    }
}
