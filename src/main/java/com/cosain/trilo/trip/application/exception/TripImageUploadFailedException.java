package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TripImageUploadFailedException extends CustomException {

    private static final String ERROR_CODE = "trip-0010";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public TripImageUploadFailedException() {}

    public TripImageUploadFailedException(String debugMessage) {
        super(debugMessage);
    }

    public TripImageUploadFailedException(Throwable cause) {
        super(cause);
    }

    public TripImageUploadFailedException(String debugMessage, Throwable cause) {
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
