package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TripAlreadyLikedException extends CustomException {

    private static final String ERROR_CODE = "trip-0012";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
