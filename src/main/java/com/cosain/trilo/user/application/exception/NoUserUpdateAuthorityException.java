package com.cosain.trilo.user.application.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoUserUpdateAuthorityException extends CustomException {

    private static final String ERROR_CODE = "user-0005";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoUserUpdateAuthorityException(){}

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
