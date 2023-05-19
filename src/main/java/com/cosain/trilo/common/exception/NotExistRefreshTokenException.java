package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotExistRefreshTokenException extends CustomException {

    private static final String ERROR_CODE = "auth-0007";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
