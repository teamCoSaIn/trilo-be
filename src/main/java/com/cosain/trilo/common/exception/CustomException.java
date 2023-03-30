package com.cosain.trilo.common.exception;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    public CustomException() {
        super();
    }

    public CustomException(String debugMessage) {
        super(debugMessage);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    public abstract String getErrorName();

    public abstract HttpStatus getHttpStatus();
}
