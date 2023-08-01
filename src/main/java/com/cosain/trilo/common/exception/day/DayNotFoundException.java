package com.cosain.trilo.common.exception.day;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DayNotFoundException extends CustomException {

    private static final String ERROR_CODE = "day-0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public DayNotFoundException() {
    }

    public DayNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public DayNotFoundException(Throwable cause) {
        super(cause);
    }

    public DayNotFoundException(String debugMessage, Throwable cause) {
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
