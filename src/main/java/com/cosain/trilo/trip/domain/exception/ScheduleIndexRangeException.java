package com.cosain.trilo.trip.domain.exception;

public class ScheduleIndexRangeException extends RuntimeException {

    public ScheduleIndexRangeException() {
    }

    public ScheduleIndexRangeException(String message) {
        super(message);
    }

    public ScheduleIndexRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
