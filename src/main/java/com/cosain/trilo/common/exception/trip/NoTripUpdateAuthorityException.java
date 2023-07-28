package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTripUpdateAuthorityException extends CustomException {

    private static final String ERROR_CODE = "trip-0004";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripUpdateAuthorityException() {}

    public NoTripUpdateAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoTripUpdateAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoTripUpdateAuthorityException(String debugMessage, Throwable cause) {
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
