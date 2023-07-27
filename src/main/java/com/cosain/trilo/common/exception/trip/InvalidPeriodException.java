package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidPeriodException extends CustomException {

    private static final String ERROR_CODE = "trip-0005";
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
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
