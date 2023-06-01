package com.cosain.trilo.trip.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NullTripIdException extends CustomException {

    private static final String ERROR_CODE = "schedule-0007"; // 일정 생성시 TripId를 안 넘겼을 때
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NullTripIdException() {
    }

    public NullTripIdException(String debugMessage) {
        super(debugMessage);
    }

    public NullTripIdException(Throwable cause) {
        super(cause);
    }

    public NullTripIdException(String debugMessage, Throwable cause) {
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
