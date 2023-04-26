package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTripUpdateAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoTripUpdateAuthority";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripUpdateAuthorityException() {}

    public NoTripUpdateAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoTripUpdateAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoTripUpdateAuthorityException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
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
