package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class TokenAuthenticationFilterException extends CustomException {

    private static final String ERROR_NAME = "TokenAuthenticationFilterError";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public TokenAuthenticationFilterException(Throwable cause) {
        super(cause);
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
