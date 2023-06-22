package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NoFileNameException extends CustomException {

    private static final String ERROR_CODE = "file-0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NoFileNameException() {
    }

    public NoFileNameException(String debugMessage) {
        super(debugMessage);
    }

    public NoFileNameException(Throwable cause) {
        super(cause);
    }

    public NoFileNameException(String debugMessage, Throwable cause) {
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
