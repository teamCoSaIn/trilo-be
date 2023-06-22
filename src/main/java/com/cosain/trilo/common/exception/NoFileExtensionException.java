package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public class NoFileExtensionException extends CustomException{

    private static final String ERROR_CODE = "file-0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NoFileExtensionException() {
    }

    public NoFileExtensionException(String debugMessage) {
        super(debugMessage);
    }

    public NoFileExtensionException(Throwable cause) {
        super(cause);
    }

    public NoFileExtensionException(String debugMessage, Throwable cause) {
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
