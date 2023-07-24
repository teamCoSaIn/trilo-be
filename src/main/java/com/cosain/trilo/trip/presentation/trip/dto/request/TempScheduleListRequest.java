package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.Getter;

@Getter
public class TempScheduleListRequest {

    private final Long scheduleId;
    private final Integer size;

    public TempScheduleListRequest(Long scheduleId, Integer size){
        this.scheduleId = scheduleId;
        this.size = size;
    }
}
