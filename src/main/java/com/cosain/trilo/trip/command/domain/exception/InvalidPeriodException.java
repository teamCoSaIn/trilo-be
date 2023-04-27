package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidPeriodException extends CustomException {

    private static final String ERROR_NAME = "InvalidPeriod";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidPeriodException() {
    }

    public InvalidPeriodException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidPeriodException(Throwable cause) {
        super(cause);
    }

    public InvalidPeriodException(String debugMessage, Throwable cause) {
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
