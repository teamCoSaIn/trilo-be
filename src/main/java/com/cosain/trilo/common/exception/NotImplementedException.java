package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotImplementedException extends CustomException {

    private static final String ERROR_NAME = "NotImplemented";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public NotImplementedException(String debugMessage) {
        super(debugMessage);
    }

    @Override
    public String getErrorName() {
        return ERROR_NAME;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
