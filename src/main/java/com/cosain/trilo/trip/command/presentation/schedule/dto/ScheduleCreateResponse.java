package com.cosain.trilo.trip.command.presentation.schedule.dto;

import lombok.Getter;

@Getter
public class ScheduleCreateResponse {

    private Long scheduleId;

    public static ScheduleCreateResponse from(Long scheduleId){
        return new ScheduleCreateResponse(scheduleId);
    }

    private ScheduleCreateResponse(Long scheduleId){
        this.scheduleId = scheduleId;
    }

}
