package com.cosain.trilo.trip.presentation.schedule.command.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleMoveRequest {

    private Long targetDayId;
    private Integer targetOrder;

    public ScheduleMoveRequest(Long targetDayId, Integer targetOrder) {
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }
}
