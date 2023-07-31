package com.cosain.trilo.common.exception.schedule;

public class MidScheduleIndexConflictException extends RuntimeException {

    public MidScheduleIndexConflictException() {
    }

    public MidScheduleIndexConflictException(String message) {
        super(message);
    }

    public MidScheduleIndexConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
