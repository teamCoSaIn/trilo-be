package com.cosain.trilo.common.exception;

public class NotSupportClientIdException extends RuntimeException{

    private final static String MESSAGE = "지원하지 않는 로그인 방식입니다.";

     public NotSupportClientIdException(){
         super(MESSAGE);
     }
}
