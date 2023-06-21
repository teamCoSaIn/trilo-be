package com.cosain.trilo.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenAnalyzer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenAnalyzer implements TokenAnalyzer {

    private final Key secretKey;

    public JwtTokenAnalyzer(@Value("${jwt.secret-key}") String secretKey){
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
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public LocalDateTime getTokenExpiryDateTime(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public Long getTokenRemainExpiryFrom(String token) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - (new Date().getTime());
    }
}
