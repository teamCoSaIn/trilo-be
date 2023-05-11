package com.cosain.trilo.trip.command.application.result;

import com.cosain.trilo.trip.command.domain.dto.ScheduleMoveDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleMoveResult {

    private Long scheduleId;
    private Long beforeDayId;
    private Long afterDayId;
    private boolean positionChanged;

    public static ScheduleMoveResult from(ScheduleMoveDto dto) {
        return ScheduleMoveResult.builder()
                .scheduleId(dto.getScheduleId())
                .beforeDayId(dto.getBeforeDayId())
                .afterDayId(dto.getAfterDayId())
                .positionChanged(dto.isPositionChanged())
                .build();
    }

    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleMoveResult(Long scheduleId, Long beforeDayId, Long afterDayId, boolean positionChanged) {
        this.scheduleId = scheduleId;
        this.beforeDayId = beforeDayId;
        this.afterDayId = afterDayId;
        this.positionChanged = positionChanged;
    }
}
