package com.cosain.trilo.auth.presentation.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenStatusResponse {
    private boolean availability;
    private LocalDateTime expiryDateTime;

    public static TokenStatusResponse of(boolean availability, LocalDateTime expiryDateTime){
        return new TokenStatusResponse(availability, expiryDateTime);
    }

    private TokenStatusResponse(boolean availability, LocalDateTime expiryDateTime){
        this.availability = availability;
        this.expiryDateTime = expiryDateTime;
    }
}
