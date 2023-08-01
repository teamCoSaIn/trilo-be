package com.cosain.trilo.common.exception.schedule;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoScheduleCreateAuthorityException extends CustomException {

    private static final String ERROR_CODE = "schedule-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoScheduleCreateAuthorityException() {
    }

    public NoScheduleCreateAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoScheduleCreateAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoScheduleCreateAuthorityException(String debugMessage, Throwable cause) {
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
