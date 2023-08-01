package com.cosain.trilo.trip.presentation.schedule.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일정 이동을 위한 요청 정보를 이 객체에 바인딩합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleMoveRequest {

    /**
     * 도착지의 Day 식별자(id)
     */
    private Long targetDayId;

    /**
     * 도착지의 몇 번째 순서로 이동할 것인지
     */
    private Integer targetOrder;

    /**
     * 일정 이동 요청을 생성합니다.
     *
     * @param targetDayId 도착지의 Day 식별자(id)
     * @param targetOrder 도착지의 몇 번째 순서로 이동할 것인지
     */
    public ScheduleMoveRequest(Long targetDayId, Integer targetOrder) {
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }
}
