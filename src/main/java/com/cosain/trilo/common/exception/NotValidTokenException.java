package com.cosain.trilo.common.exception;

public class NotValidTokenException extends RuntimeException{
    private final static String MESSAGE = "토큰이 유효하지 않습니다";
    public NotValidTokenException(){
        super(MESSAGE);
    }
}
