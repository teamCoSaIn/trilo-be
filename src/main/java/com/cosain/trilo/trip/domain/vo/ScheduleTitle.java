package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.common.exception.schedule.InvalidScheduleTitleException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 일정의 제목 VO(값 객체)입니다.
 */
@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleTitle {

    /**
     * 일정 제목의 최대 길이
     */
    private static final int MAX_LENGTH = 35;

    /**
     * 일정 제목의 원시값
     */
    @Column(name = "schedule_title")
    private String value;

    /**
     * 일정 제목 VO(값 객체)를 생성합니다.
     * @param value 일정 제목 원시값
     * @return 일정 제목 VO (값 객체)
     * @throws InvalidScheduleTitleException 일정 제목이 유효하지 않을 때
     */
    public static ScheduleTitle of(String value) throws InvalidScheduleTitleException {
        requireNotNullTitle(value); // null 허용 안 함
        requireSafeRangeLength(value); // 길이제한 검증

        return new ScheduleTitle(value);
    }

    /**
     * 일정의 제목을 확인하여, null 일 경우 예외를 발생시킵니다.
     * @param value 일정 제목
     * @throws InvalidScheduleTitleException 일정 제목이 null 일 때
     */
    private static void requireNotNullTitle(String value) throws InvalidScheduleTitleException {
        if (value == null) {
            throw new InvalidScheduleTitleException("일정 제목은 null일 수 없음");
        }
    }

    /**
     * 일정의 제목 길이를 확인하여, 최대 길이보다 길 경우 예외를 발생시킵니다.
     * @param value 일정 제목
     * @throws InvalidScheduleTitleException 일정 길이가 너무 길 때
     */
    private static void requireSafeRangeLength(String value) throws InvalidScheduleTitleException {
        if (value.length() > MAX_LENGTH) {
            throw new InvalidScheduleTitleException(String.format("일정의 제목은 $d자 이하여야 함.", MAX_LENGTH));
        }
    }

    private ScheduleTitle(String value) {
        this.value = value;
    }
}
