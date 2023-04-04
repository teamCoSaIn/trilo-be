package com.cosain.trilo.common.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    private ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(String errorCode, String errorMessage){
        return new ErrorResponse(errorCode, errorMessage);
    }
}
