package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class EmptyFileException extends CustomException {

    private static final String ERROR_CODE = "file-0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;


    public EmptyFileException() {
    }

    public EmptyFileException(String debugMessage) {
        super(debugMessage);
    }

    public EmptyFileException(Throwable cause) {
        super(cause);
    }

    public EmptyFileException(String debugMessage, Throwable cause) {
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
