package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotExistRefreshTokenException extends CustomException {

    private static final String ERROR_NAME = "NotExistRefreshToken";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    @Override
    public String getErrorName() {
        return ERROR_NAME;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
