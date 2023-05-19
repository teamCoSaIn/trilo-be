package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class TokenAuthenticationFilterException extends CustomException {

    private static final String ERROR_CODE = "auth-0006";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public TokenAuthenticationFilterException(Throwable cause) {
        super(cause);
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
