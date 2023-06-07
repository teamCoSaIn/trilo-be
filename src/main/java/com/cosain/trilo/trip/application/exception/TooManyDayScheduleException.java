package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TooManyDayScheduleException extends CustomException {

    private static final String ERROR_CODE = "schedule-0010";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public TooManyDayScheduleException() {
    }

    public TooManyDayScheduleException(String debugMessage) {
        super(debugMessage);
    }

    public TooManyDayScheduleException(Throwable cause) {
        super(cause);
    }

    public TooManyDayScheduleException(String debugMessage, Throwable cause) {
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
