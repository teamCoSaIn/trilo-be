package com.cosain.trilo.user.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidNickNameException extends CustomException {

    private static final String ERROR_CODE = "user-0006";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidNickNameException() {
        super();
    }

    public InvalidNickNameException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidNickNameException(Throwable cause) {
        super(cause);
    }

    public InvalidNickNameException(String debugMessage, Throwable cause) {
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
