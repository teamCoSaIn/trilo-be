package com.cosain.trilo.trip.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidDayColorNameException extends CustomException {

    private static final String ERROR_CODE = "day-0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidDayColorNameException() {
    }

    public InvalidDayColorNameException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidDayColorNameException(Throwable cause) {
        super(cause);
    }

    public InvalidDayColorNameException(String debugMessage, Throwable cause) {
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
