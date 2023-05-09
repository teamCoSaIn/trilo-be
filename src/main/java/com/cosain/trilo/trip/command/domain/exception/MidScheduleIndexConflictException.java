package com.cosain.trilo.trip.command.domain.exception;

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
