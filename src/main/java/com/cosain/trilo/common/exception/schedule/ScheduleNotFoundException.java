package com.cosain.trilo.common.exception.schedule;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends CustomException {

    private static final String ERROR_CODE = "schedule-0001";
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
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
