package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotSupportClientIdException extends CustomException {

    private static final String ERROR_NAME = "NotSupportClientId";
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
