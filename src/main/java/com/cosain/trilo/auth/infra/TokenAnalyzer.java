package com.cosain.trilo.auth.infra;

import com.cosain.trilo.auth.infra.jwt.UserPayload;

public interface TokenAnalyzer {
    boolean validateToken(String token);
    boolean isValidToken(String authorizationHeader);
    UserPayload getPayload(String authorizationHeader);
    Long getUserIdFromToken(String token);
    Long getTokenRemainExpiryFrom(String token);
}