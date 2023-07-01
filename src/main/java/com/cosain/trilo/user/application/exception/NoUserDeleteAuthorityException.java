package com.cosain.trilo.user.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoUserDeleteAuthorityException extends CustomException {

    private static final String ERROR_CODE = "user-0004";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoUserDeleteAuthorityException() {
    }

    public NoUserDeleteAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoUserDeleteAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoUserDeleteAuthorityException(String debugMessage, Throwable cause) {
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
