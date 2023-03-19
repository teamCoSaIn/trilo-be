package com.cosain.trilo.common.exception;

public class TokenAuthenticationFilterException extends RuntimeException {

    private static final String MESSAGE = "토큰 검증 과정에서 문제가 발생했습니다.";

    public TokenAuthenticationFilterException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
