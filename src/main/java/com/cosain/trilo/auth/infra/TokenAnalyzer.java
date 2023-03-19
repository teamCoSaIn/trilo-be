package com.cosain.trilo.auth.infra;

public interface TokenAnalyzer {
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    Long getTokenExpiryFrom(String token);
}