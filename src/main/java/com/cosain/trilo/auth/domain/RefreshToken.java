package com.cosain.trilo.auth.domain;


import org.springframework.data.redis.core.RedisHash;

@RedisHash("refreshToken")
public class RefreshToken extends Token{

    private RefreshToken(String id, Long expiry){
        super(id, expiry);
    }
    public static RefreshToken of(String refreshToken, Long expiry){
        return new RefreshToken(refreshToken, expiry);
    }
}
