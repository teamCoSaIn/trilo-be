package com.cosain.trilo.auth.application;

import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import com.cosain.trilo.auth.domain.LogoutAccessToken;
import com.cosain.trilo.auth.domain.RefreshToken;
import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.common.exception.NotExistRefreshTokenException;
import com.cosain.trilo.common.exception.NotValidTokenException;
import com.cosain.trilo.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final OAuthProfileRequestService OAuthProfileRequestService;
    private final UserService userService;

    @Transactional
    public ReIssueAccessTokenResult reissueAccessToken(String refreshToken){
        checkIfValidTokenOrThrow(refreshToken);
        checkTokenExistenceOrThrow(refreshToken);
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        String accessToken = jwtProvider.createAccessToken(userId);
        return ReIssueAccessTokenResult.of(accessToken);
    }
    private void checkIfValidTokenOrThrow(String refreshToken){
        if(!jwtProvider.isValidRefreshToken(refreshToken)){
            throw new NotValidTokenException();
        }
    }
    private void checkTokenExistenceOrThrow(String refreshToken){
        if(!tokenRepository.existsRefreshTokenById(refreshToken)){
            throw new NotExistRefreshTokenException();
        }
    }

    public RefreshTokenStatusResponse createTokenStatus(String token) {
        boolean availability = jwtProvider.isValidRefreshToken(token);
        return RefreshTokenStatusResponse.from(availability);
    }


    @Transactional
    public void logout(String authHeaderValue, String refreshToken) {
        String accessToken = getAccessTokenFrom(authHeaderValue);
        Long remainExpiry = jwtProvider.getTokenRemainExpiry(accessToken);
        tokenRepository.saveLogoutAccessToken(LogoutAccessToken.of(accessToken, remainExpiry));
        tokenRepository.deleteRefreshTokenById(refreshToken);
    }

    private String getAccessTokenFrom(String authHeaderValue){
        return authHeaderValue.substring(7);
    }

    @Transactional
    public LoginResult login(OAuthLoginParams oAuthLoginParams){

        OAuthProfileDto oAuthProfileDto = getUserProfileResponse(oAuthLoginParams);
        Long userId = userService.createOrUpdate(oAuthProfileDto);

        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        Long tokenExpiry = jwtProvider.getTokenRemainExpiry(refreshToken);
        tokenRepository.saveRefreshToken(RefreshToken.of(refreshToken, tokenExpiry));

        return LoginResult.of(accessToken, refreshToken);
    }

    private OAuthProfileDto getUserProfileResponse(OAuthLoginParams oAuthLoginParams) {
        return OAuthProfileRequestService.request(oAuthLoginParams);
    }

}
