package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTripDayException extends CustomException {

    private static final String ERROR_CODE = "day-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidTripDayException() {
    }

    public InvalidTripDayException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidTripDayException(Throwable cause) {
        super(cause);
    }

    public InvalidTripDayException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
