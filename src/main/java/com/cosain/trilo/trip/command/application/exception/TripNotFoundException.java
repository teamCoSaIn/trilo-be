package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TripNotFoundException extends CustomException {

    private static final String ERROR_NAME = "TripNotFound";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public TripNotFoundException() {}

    public TripNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public TripNotFoundException(Throwable cause) {
        super(cause);
    }

    public TripNotFoundException(String debugMessage, Throwable cause) {
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