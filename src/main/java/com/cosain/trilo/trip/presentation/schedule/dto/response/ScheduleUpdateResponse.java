package com.cosain.trilo.trip.presentation.schedule.dto.response;

import lombok.Getter;

@Getter
public class ScheduleUpdateResponse {
    private Long scheduleId;
    public static ScheduleUpdateResponse from(Long scheduleId){
        return new ScheduleUpdateResponse(scheduleId);
    }
    private ScheduleUpdateResponse(Long scheduleId){
        this.scheduleId = scheduleId;
    }
}
