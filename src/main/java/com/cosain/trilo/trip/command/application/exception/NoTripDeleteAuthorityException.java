package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTripDeleteAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoTripDeleteAuthority";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripDeleteAuthorityException() {
    }

    public NoTripDeleteAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoTripDeleteAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoTripDeleteAuthorityException(String debugMessage, Throwable cause) {
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
