package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TripNotFoundException extends CustomException {

    private static final String ERROR_CODE = "trip-0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public TripNotFoundException() {}

    public TripNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public TripNotFoundException(Throwable cause) {
        super(cause);
    }

    public TripNotFoundException(String debugMessage, Throwable cause) {
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
