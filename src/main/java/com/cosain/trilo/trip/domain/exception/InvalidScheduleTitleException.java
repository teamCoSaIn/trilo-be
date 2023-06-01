package com.cosain.trilo.trip.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidScheduleTitleException extends CustomException {

    private static final String ERROR_CODE = "schedule-0008";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidScheduleTitleException() {
    }

    public InvalidScheduleTitleException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidScheduleTitleException(Throwable cause) {
        super(cause);
    }

    public InvalidScheduleTitleException(String debugMessage, Throwable cause) {
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
