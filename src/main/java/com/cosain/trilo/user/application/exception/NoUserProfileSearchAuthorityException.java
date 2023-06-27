package com.cosain.trilo.user.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoUserProfileSearchAuthorityException extends CustomException {

    private static final String ERROR_CODE = "user-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoUserProfileSearchAuthorityException() {
    }

    public NoUserProfileSearchAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoUserProfileSearchAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoUserProfileSearchAuthorityException(String debugMessage, Throwable cause) {
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
