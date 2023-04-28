package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DayNotFoundException extends CustomException {

    private static final String ERROR_NAME = "DayNotFoundException";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public DayNotFoundException() {
    }

    public DayNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public DayNotFoundException(Throwable cause) {
        super(cause);
    }

    public DayNotFoundException(String debugMessage, Throwable cause) {
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
