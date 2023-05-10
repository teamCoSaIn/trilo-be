package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoScheduleMoveAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoScheduleUpdateAuthorityException";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoScheduleMoveAuthorityException() {
    }

    public NoScheduleMoveAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoScheduleMoveAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoScheduleMoveAuthorityException(String debugMessage, Throwable cause) {
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
