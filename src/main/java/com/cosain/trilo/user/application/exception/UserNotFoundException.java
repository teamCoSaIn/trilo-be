package com.cosain.trilo.user.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {

    private static final String ERROR_CODE = "user-0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public UserNotFoundException() {
    }

    public UserNotFoundException(String debugMessage) {
        super(debugMessage);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException(String debugMessage, Throwable cause) {
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
