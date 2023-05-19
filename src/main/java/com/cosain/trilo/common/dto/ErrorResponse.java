package com.cosain.trilo.common.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String errorDetail;

    public static ErrorResponse of(String errorCode, String errorMessage, String errorDetail){
        return new ErrorResponse(errorCode, errorMessage, errorDetail);
    }

    public ErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }
}
