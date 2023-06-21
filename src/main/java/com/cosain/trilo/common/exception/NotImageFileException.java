package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotImageFileException extends CustomException {

    private static final String ERROR_CODE = "file-0005";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotImageFileException() {
    }

    public NotImageFileException(String debugMessage) {
        super(debugMessage);
    }

    public NotImageFileException(Throwable cause) {
        super(cause);
    }

    public NotImageFileException(String debugMessage, Throwable cause) {
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
