package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {

    private static final String errorName = "UserNotFound";
    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    @Override
    public String getErrorName() {
        return errorName;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
