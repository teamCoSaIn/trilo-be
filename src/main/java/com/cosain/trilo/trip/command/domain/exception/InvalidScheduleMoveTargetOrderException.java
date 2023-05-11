package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidScheduleMoveTargetOrderException extends CustomException {

    private static final String ERROR_NAME = "InvalidScheduleMoveTargetOrderException";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidScheduleMoveTargetOrderException() {
    }

    public InvalidScheduleMoveTargetOrderException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidScheduleMoveTargetOrderException(Throwable cause) {
        super(cause);
    }

    public InvalidScheduleMoveTargetOrderException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    @Override
    public String getErrorName() {
        return ERROR_NAME;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
