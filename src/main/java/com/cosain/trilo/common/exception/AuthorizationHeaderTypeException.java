package com.cosain.trilo.common.exception;

public class AuthorizationHeaderTypeException extends RuntimeException {

    private final static String MESSAGE = "인증 헤더 타입이 올바르지 않습니다.";

    public AuthorizationHeaderTypeException() {
        super(MESSAGE);
    }
}
