package com.cosain.trilo.common.dto;

import lombok.Getter;

@Getter
public class BasicErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String errorDetail;

    public static BasicErrorResponse of(String errorCode, String errorMessage, String errorDetail){
        return new BasicErrorResponse(errorCode, errorMessage, errorDetail);
    }

    public BasicErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }
}
