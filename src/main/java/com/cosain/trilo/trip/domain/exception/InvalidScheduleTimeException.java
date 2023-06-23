package com.cosain.trilo.trip.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidScheduleTimeException extends CustomException {

    private static final String ERROR_CODE = "schedule-0011";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidScheduleTimeException() {
    }

    public InvalidScheduleTimeException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidScheduleTimeException(Throwable cause) {
        super(cause);
    }

    public InvalidScheduleTimeException(String debugMessage, Throwable cause) {
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
