package com.cosain.trilo.trip.query.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTripDetailSearchAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoTripDetailSearchAuthorityException";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripDetailSearchAuthorityException(String debugMessage) {
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
