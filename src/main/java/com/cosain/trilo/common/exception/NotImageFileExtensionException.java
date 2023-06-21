package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NotImageFileExtensionException extends CustomException {

    private static final String ERROR_CODE = "file-0004";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotImageFileExtensionException() {
    }

    public NotImageFileExtensionException(String debugMessage) {
        super(debugMessage);
    }

    public NotImageFileExtensionException(Throwable cause) {
        super(cause);
    }

    public NotImageFileExtensionException(String debugMessage, Throwable cause) {
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
