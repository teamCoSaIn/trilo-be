package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 일정 이동의 결과를 바인딩한 객체입니다.
 */
@Getter
public class ScheduleMoveResult {

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
     * @param dto : ScheduleMoveDto
     * @return 일정 이동 결과
     */
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
