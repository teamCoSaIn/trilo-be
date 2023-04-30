package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoScheduleDeleteAuthorityException extends CustomException {

    private static final String ERROR_NAME = "NoScheduleDeleteAuthority";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoScheduleDeleteAuthorityException() {
    }

    public NoScheduleDeleteAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoScheduleDeleteAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoScheduleDeleteAuthorityException(String debugMessage, Throwable cause) {
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
