package com.cosain.trilo.trip.presentation.trip.query.dto.request;

import lombok.Getter;

@Getter
public class TempSchedulePageCondition {

    private Long scheduleId;

    public TempSchedulePageCondition(Long scheduleId){
        this.scheduleId = scheduleId;
    }
}
