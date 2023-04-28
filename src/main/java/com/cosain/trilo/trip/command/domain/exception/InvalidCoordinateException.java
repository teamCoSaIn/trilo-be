package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidCoordinateException extends CustomException {

    private static final String ERROR_NAME = "InvalidCoordinate";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidCoordinateException() {
    }

    public InvalidCoordinateException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidCoordinateException(Throwable cause) {
        super(cause);
    }

    public InvalidCoordinateException(String debugMessage, Throwable cause) {
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
