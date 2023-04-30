package com.cosain.trilo.trip.command.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends CustomException {

    private static final String ERROR_NAME = "ScheduleNotFoundException";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ScheduleNotFoundException(String debugMessage) {
        super(debugMessage);
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
