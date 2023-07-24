package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTripTitleException extends CustomException {

    private static final String ERROR_CODE = "trip-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidTripTitleException() {
    }

    public InvalidTripTitleException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidTripTitleException(Throwable cause) {
        super(cause);
    }

    public InvalidTripTitleException(String debugMessage, Throwable cause) {
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
