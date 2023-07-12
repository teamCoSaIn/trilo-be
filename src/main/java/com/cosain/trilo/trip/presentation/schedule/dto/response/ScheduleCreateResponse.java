package com.cosain.trilo.trip.presentation.schedule.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateResponse {

    private Long scheduleId;

    public static ScheduleCreateResponse from(Long scheduleId){
        return new ScheduleCreateResponse(scheduleId);
    }

    private ScheduleCreateResponse(Long scheduleId){
        this.scheduleId = scheduleId;
    }

}
