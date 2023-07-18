package com.cosain.trilo.auth.application.token;

public interface JwtProvider {
    String createAccessToken(Long userId);
    String createRefreshToken(Long userId);
    boolean isValidAccessToken(String authorizationHeader);

    boolean isValidRefreshToken(String token);

    UserPayload getPayload(String authorizationHeader);

    Long getUserIdFromToken(String token);

    Long getTokenRemainExpiry(String token);

}
