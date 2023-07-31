package com.cosain.trilo.trip.domain.dto;

import com.cosain.trilo.trip.domain.entity.Day;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 일정 이동의 결과를 바인딩한 객체입니다.
 */
@Getter
public class ScheduleMoveDto {

    /**
     * 일정의 id(식별자)
     */
    private Long scheduleId;

    /**
     * 일정이 옮겨기지 전 속해있던 Day id
     */
    private Long beforeDayId;

    /**
     * 일정이 옮겨진 후에 속해있는 Day id
     */
    private Long afterDayId;

    /**
     * 일정의 위치가 실제로 변했는 지 여부(같은 위치에서 제자리 이동했는 지 여부 확인)
     */
    private boolean positionChanged;

    /**
     * 일정의 위치가 변경됐을 때의, 일정 이동 결과를 생성합니다.
     * @param scheduleId 일정의 id
     * @param beforeDay 일정이 기존에 속해있던 Day
     * @param afterDay 일정이 옮겨진 후에 속해있는 Day
     * @return 일정 이동 결과
     */
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

    /**
     * 일정의 위치가 변경되지 않았을 때의, 일정 이동 결과를 생성합니다.
     * @param scheduleId 일정의 id
     * @param day 일정이 속해있는 Day
     * @return 일정 이동 결과
     */
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
