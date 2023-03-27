package com.cosain.trilo.auth.presentation.dto;

import lombok.Getter;


@Getter
public class RefreshTokenStatusResponse {
    private boolean availability;

    public static RefreshTokenStatusResponse from(boolean availability){
        return new RefreshTokenStatusResponse(availability);
    }

    private RefreshTokenStatusResponse(boolean availability){
        this.availability = availability;
    }
}
