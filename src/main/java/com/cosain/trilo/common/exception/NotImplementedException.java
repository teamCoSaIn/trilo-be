package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotImplementedException extends CustomException {

    private static final String ERROR_CODE = "server-9999";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public NotImplementedException(String debugMessage) {
        super(debugMessage);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
