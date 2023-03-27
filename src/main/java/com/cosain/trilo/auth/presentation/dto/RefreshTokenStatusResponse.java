package com.cosain.trilo.auth.presentation.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshTokenStatusResponse {
    private boolean availability;
    private LocalDateTime expiryDateTime;

    public static RefreshTokenStatusResponse of(boolean availability, LocalDateTime expiryDateTime){
        return new RefreshTokenStatusResponse(availability, expiryDateTime);
    }

    private RefreshTokenStatusResponse(boolean availability, LocalDateTime expiryDateTime){
        this.availability = availability;
        this.expiryDateTime = expiryDateTime;
    }
}
