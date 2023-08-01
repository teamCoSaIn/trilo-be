package com.cosain.trilo.trip.presentation.schedule.dto.response;

import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>일정 이동 응답을 바인딩할 객체입니다.</p>
 * <p>일정 이동 결과 정보가 바인딩됩니다.</p>
 * <p>이 객체는 정적 팩터리 메서드인 {@link #from(ScheduleMoveResult)} 으로 생성하세요.</p>
 */
@Getter
public class ScheduleMoveResponse {

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
     * @param scheduleMoveResult : ScheduleMoveResult
     * @return 일정 이동 응답
     */
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
