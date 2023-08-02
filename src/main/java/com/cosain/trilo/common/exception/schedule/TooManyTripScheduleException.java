package com.cosain.trilo.common.exception.schedule;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TooManyTripScheduleException extends CustomException {

    private static final String ERROR_CODE = "schedule-0009";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public TooManyTripScheduleException() {
    }

    public TooManyTripScheduleException(String debugMessage) {
        super(debugMessage);
    }

    public TooManyTripScheduleException(Throwable cause) {
        super(cause);
    }

    public TooManyTripScheduleException(String debugMessage, Throwable cause) {
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
