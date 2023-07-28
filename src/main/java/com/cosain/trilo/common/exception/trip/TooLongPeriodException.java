package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TooLongPeriodException extends CustomException {

    private static final String ERROR_CODE = "trip-0009";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public TooLongPeriodException() {
    }

    public TooLongPeriodException(String debugMessage) {
        super(debugMessage);
    }

    public TooLongPeriodException(Throwable cause) {
        super(cause);
    }

    public TooLongPeriodException(String debugMessage, Throwable cause) {
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
