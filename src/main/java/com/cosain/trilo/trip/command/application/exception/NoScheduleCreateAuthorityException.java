package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoScheduleCreateAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoTripCreateAuthority";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoScheduleCreateAuthorityException() {
    }

    public NoScheduleCreateAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoScheduleCreateAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoScheduleCreateAuthorityException(String debugMessage, Throwable cause) {
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
