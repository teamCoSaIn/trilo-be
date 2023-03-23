package com.cosain.trilo.common.exception;

public class NotExistRefreshTokenException extends RuntimeException{

    private final static String MESSAGE = "토큰이 존재하지 않습니다.";
    public NotExistRefreshTokenException(){
        super(MESSAGE);
    }
}
