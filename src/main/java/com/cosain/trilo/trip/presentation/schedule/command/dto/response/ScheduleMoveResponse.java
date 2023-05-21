package com.cosain.trilo.trip.presentation.schedule.command.dto.response;

import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleMoveResult;
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
