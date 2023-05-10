package com.cosain.trilo.trip.command.presentation.schedule.dto;

import lombok.Getter;

@Getter
public class ScheduleMoveResponse {

    private Long scheduleId;

    public static ScheduleMoveResponse from(Long scheduleId) {
        return new ScheduleMoveResponse(scheduleId);
    }

    private ScheduleMoveResponse(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
