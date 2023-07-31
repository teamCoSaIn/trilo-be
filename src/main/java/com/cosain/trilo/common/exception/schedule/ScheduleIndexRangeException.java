package com.cosain.trilo.common.exception.schedule;

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
