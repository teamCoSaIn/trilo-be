package com.cosain.trilo.trip.query.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TripperNotFoundException extends CustomException {

    private static final String ERROR_NAME = "TripperNotFound";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public TripperNotFoundException() {}

    public TripperNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public TripperNotFoundException(Throwable cause) {
        super(cause);
    }

    public TripperNotFoundException(String debugMessage, Throwable cause) {
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
