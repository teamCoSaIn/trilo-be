package com.cosain.trilo.auth.infra.jwt;

import com.cosain.trilo.auth.infra.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
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


    @Override
    public String createAccessTokenById(Long id){

        return createToken(id, accessTokenExpiryMs, ACCESS_TOKEN_SUBJECT);
    }

    @Override
    public String createRefreshTokenById(Long id) {

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
}
