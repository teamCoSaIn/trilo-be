package com.cosain.trilo.trip.command.presentation.schedule.dto;

import com.cosain.trilo.trip.command.application.result.ScheduleMoveResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleMoveResponse {

    private Long scheduleId;
    private Long beforeDayId;
    private Long afterDayId;
    private boolean positionChanged;

    public static ScheduleMoveResponse from(ScheduleMoveResult scheduleMoveResult) {
        return ScheduleMoveResponse.builder()
                .scheduleId(scheduleMoveResult.getScheduleId())
                .beforeDayId(scheduleMoveResult.getBeforeDayId())
                .afterDayId(scheduleMoveResult.getAfterDayId())
                .positionChanged(scheduleMoveResult.isPositionChanged())
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleMoveResponse(Long scheduleId, Long beforeDayId, Long afterDayId, boolean positionChanged) {
        this.scheduleId = scheduleId;
        this.beforeDayId = beforeDayId;
        this.afterDayId = afterDayId;
        this.positionChanged = positionChanged;
    }
}
