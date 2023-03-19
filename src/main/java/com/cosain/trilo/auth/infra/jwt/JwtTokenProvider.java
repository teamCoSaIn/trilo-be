package com.cosain.trilo.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.config.security.dto.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final Key secretKey;

    public JwtTokenProvider(
            @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry,
            @Value("${jwt.secret-key}") String secretKey
    ){
        this.accessTokenExpiryMs = accessTokenExpiry;
        this.refreshTokenExpiryMs = refreshTokenExpiry;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(final Authentication authentication){
        return createToken(authentication, accessTokenExpiryMs);
    }

    public String createRefreshToken(final Authentication authentication){
        return createToken(authentication, refreshTokenExpiryMs);
    }

    private String createToken(final Authentication authentication,final long tokenExpiryMs){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Date nowDate = new Date();
        Date endDate = new Date(nowDate.getTime() + tokenExpiryMs);

        return Jwts.builder()
                .setSubject(principal.getEmail())
                .setIssuedAt(nowDate)
                .setExpiration(endDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
