package com.cosain.trilo.trip.query.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends CustomException {

    private static final String ERROR_NAME = "ScheduleNotFound";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ScheduleNotFoundException() {
    }

    public ScheduleNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public ScheduleNotFoundException(Throwable cause) {
        super(cause);
    }

    public ScheduleNotFoundException(String debugMessage, Throwable cause) {
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
