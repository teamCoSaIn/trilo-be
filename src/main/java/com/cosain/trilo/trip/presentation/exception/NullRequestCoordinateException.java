package com.cosain.trilo.trip.presentation.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NullRequestCoordinateException extends CustomException {

    private static final String ERROR_CODE = "place-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NullRequestCoordinateException() {
    }

    public NullRequestCoordinateException(String debugMessage) {
        super(debugMessage);
    }

    public NullRequestCoordinateException(Throwable cause) {
        super(cause);
    }

    public NullRequestCoordinateException(String debugMessage, Throwable cause) {
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
