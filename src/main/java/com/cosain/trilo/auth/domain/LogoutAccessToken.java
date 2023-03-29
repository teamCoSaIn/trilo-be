package com.cosain.trilo.auth.domain;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("logoutAccessToken")
public class LogoutAccessToken extends Token{

    private LogoutAccessToken(String id, Long expiry){
        super(id, expiry);
    }

    public static LogoutAccessToken of(String accessToken, Long expiry){
        return new LogoutAccessToken(accessToken, expiry);
    }
}
