package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EmptyPeriodUpdateException extends CustomException {

    private static final String ERROR_NAME = "InvalidTripPeriod";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public EmptyPeriodUpdateException() {
    }

    public EmptyPeriodUpdateException(String debugMessage) {
        super(debugMessage);
    }

    public EmptyPeriodUpdateException(Throwable cause) {
        super(cause);
    }

    public EmptyPeriodUpdateException(String debugMessage, Throwable cause) {
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
