package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class LockNotAcquiredException extends CustomException{

    private static final String ERROR_CODE = "lock-0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.LOCKED;
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
