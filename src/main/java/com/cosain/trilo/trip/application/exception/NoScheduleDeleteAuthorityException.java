package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoScheduleDeleteAuthorityException extends CustomException {

    private static final String ERROR_CODE = "schedule-0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoScheduleDeleteAuthorityException() {
    }

    public NoScheduleDeleteAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoScheduleDeleteAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoScheduleDeleteAuthorityException(String debugMessage, Throwable cause) {
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
