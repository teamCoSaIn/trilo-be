package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class LikeNotFoundException extends CustomException {

    private static final String ERROR_CODE = "trip-0011";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
