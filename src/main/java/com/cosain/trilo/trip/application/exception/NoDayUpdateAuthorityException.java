package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoDayUpdateAuthorityException extends CustomException {

    private static final String ERROR_CODE = "day-0004";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoDayUpdateAuthorityException() {
        super();
    }

    public NoDayUpdateAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoDayUpdateAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoDayUpdateAuthorityException(String debugMessage, Throwable cause) {
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
