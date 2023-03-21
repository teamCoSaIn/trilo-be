package com.cosain.trilo.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String message;

    public static ErrorResponse from(String message){
        return ErrorResponse.builder()
                .message(message)
                .build();
    }
}
