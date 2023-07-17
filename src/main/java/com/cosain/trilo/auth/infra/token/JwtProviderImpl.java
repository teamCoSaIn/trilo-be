package com.cosain.trilo.auth.infra.token;

import com.cosain.trilo.auth.application.token.JwtProvider;
import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.common.exception.auth.TokenInvalidFormatException;
import com.cosain.trilo.common.exception.auth.TokenNotExistException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProviderImpl implements JwtProvider {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final Key secretKey;

    public JwtProviderImpl (
            @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry,
            @Value("${jwt.secret-key}") String secretKey) {
        this.accessTokenExpiryMs = accessTokenExpiry;
        this.refreshTokenExpiryMs = refreshTokenExpiry;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public boolean isValidRefreshToken(String token) {
        try{
            Claims claims = getClaims(token);
            return isRefreshToken(claims) && isNotExpired(claims);
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private boolean isRefreshToken(Claims claims) {
        return claims.getSubject().equals(REFRESH_TOKEN_SUBJECT);
    }

    @Override
    public boolean isValidAccessToken(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
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
        String token = extractToken(authorizationHeader);
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
    public Long getTokenRemainExpiry(String token) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - (new Date().getTime());
    }

    @Override
    public String createAccessToken(Long id){

        return createToken(id, accessTokenExpiryMs, ACCESS_TOKEN_SUBJECT);
    }

    @Override
    public String createRefreshToken(Long id) {

        return createToken(id, refreshTokenExpiryMs, REFRESH_TOKEN_SUBJECT);
    }

    private String createToken(Long id, long tokenExpiryMs, String subject){
        Date nowDate = new Date();
        Date endDate = new Date(nowDate.getTime() + tokenExpiryMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(nowDate)
                .setExpiration(endDate)
                .claim("id", id)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String extractToken(String authorizationHeader){
        if(authorizationHeader == null){
            throw new TokenNotExistException();
        }

        String[] splits = authorizationHeader.split(" ");
        if(splits.length != 2 || !splits[0].equalsIgnoreCase(TOKEN_TYPE)){
            throw new TokenInvalidFormatException();
        }
        return splits[1];
    }
}
