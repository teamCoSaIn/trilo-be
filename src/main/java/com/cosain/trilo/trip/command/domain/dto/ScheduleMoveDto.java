package com.cosain.trilo.trip.command.domain.dto;

import com.cosain.trilo.trip.command.domain.entity.Day;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleMoveDto {

    private Long scheduleId;
    private Long beforeDayId;
    private Long afterDayId;
    private boolean positionChanged;

    public static ScheduleMoveDto ofPositionChanged(Long scheduleId, Day beforeDay, Day afterDay) {
        Long beforeDayId = (beforeDay == null) ? null : beforeDay.getId();
        Long afterDayId = (afterDay == null) ? null : afterDay.getId();

        return ScheduleMoveDto.builder()
                .scheduleId(scheduleId)
                .beforeDayId(beforeDayId)
                .afterDayId(afterDayId)
                .positionChanged(true)
                .build();
    }

    public static ScheduleMoveDto ofNotPositionChanged(Long scheduleId, Day day) {
        Long dayId = (day == null) ? null : day.getId();

        return ScheduleMoveDto.builder()
                .scheduleId(scheduleId)
                .beforeDayId(dayId)
                .afterDayId(dayId)
                .positionChanged(false)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleMoveDto(Long scheduleId, Long beforeDayId, Long afterDayId, boolean positionChanged) {
        this.scheduleId = scheduleId;
        this.beforeDayId = beforeDayId;
        this.afterDayId = afterDayId;
        this.positionChanged = positionChanged;
    }
}
