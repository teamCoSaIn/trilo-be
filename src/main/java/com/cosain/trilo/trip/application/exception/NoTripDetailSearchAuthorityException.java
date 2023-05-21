package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTripDetailSearchAuthorityException extends CustomException {

    private static final String ERROR_CODE = "trip-0008";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripDetailSearchAuthorityException(String debugMessage) {
        super(debugMessage);
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
