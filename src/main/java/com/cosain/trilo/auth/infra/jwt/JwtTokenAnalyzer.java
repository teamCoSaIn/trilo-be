package com.cosain.trilo.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.presentation.AuthTokenExtractor;
import com.cosain.trilo.common.exception.auth.TokenInvalidFormatException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.RequiredTypeException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenAnalyzer implements TokenAnalyzer {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private final Key secretKey;
    private final AuthTokenExtractor authTokenExtractor;

    public JwtTokenAnalyzer(
            AuthTokenExtractor authTokenExtractor,
            @Value("${jwt.secret-key}") String secretKey) {
        this.authTokenExtractor = authTokenExtractor;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean isValidToken(String authorizationHeader) {
        String token = authTokenExtractor.extractToken(authorizationHeader, TOKEN_TYPE);
        try{
            Claims claims = getClaims(token);
            return isAccessToken(claims) && isNotExpired(claims);
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private boolean isAccessToken(Claims claims){
        return claims.getSubject().equals(ACCESS_TOKEN_SUBJECT);
    }

    private boolean isNotExpired(Claims claims){
        return claims.getExpiration().after(new Date());
    }
    @Override
    public UserPayload getPayload(String authorizationHeader){
        String token = authTokenExtractor.extractToken(authorizationHeader, TOKEN_TYPE);
        Claims claims = getClaims(token);
        try {
            Long id = claims.get("id", Long.class);
            return new UserPayload(id);
        }catch (RequiredTypeException | NullPointerException | IllegalArgumentException e){
            throw new TokenInvalidFormatException();
        }
    }

    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Long getTokenRemainExpiryFrom(String token) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - (new Date().getTime());
    }
}
