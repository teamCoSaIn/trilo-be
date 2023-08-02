package com.cosain.trilo.trip.presentation.schedule.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>일정 생성 응답을 바인딩할 객체입니다.</p>
 * <p>일정 생성 결과 정보가 바인딩됩니다.</p>
 * <p>이 객체는 정적 팩터리 메서드인 {@link #from(Long)} 으로 생성됩니다.</p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateResponse {

    /**
     * 생성된 일정 id(식별자)
     */
    private Long scheduleId;

    /**
     * 일정 생성 응답을 생성합니다.
     * @param scheduleId 일정 id(식별자)
     * @return 일정 생성 응답
     */
    public static ScheduleCreateResponse from(Long scheduleId){
        return new ScheduleCreateResponse(scheduleId);
    }

    private ScheduleCreateResponse(Long scheduleId){
        this.scheduleId = scheduleId;
    }

}
