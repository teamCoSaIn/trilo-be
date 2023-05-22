package com.cosain.trilo.trip.application.schedule.command.usecase.dto;

import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
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

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleMoveResult(Long scheduleId, Long beforeDayId, Long afterDayId, boolean positionChanged) {
        this.scheduleId = scheduleId;
        this.beforeDayId = beforeDayId;
        this.afterDayId = afterDayId;
        this.positionChanged = positionChanged;
    }
}
