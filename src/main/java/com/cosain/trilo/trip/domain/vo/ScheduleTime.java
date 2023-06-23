package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleTimeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalTime;

@Getter
@ToString(of ={"startTime", "endTime"})
@EqualsAndHashCode(of = {"startTime", "endTime"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleTime {

    private static final ScheduleTime DEFAULT_TIME = ScheduleTime.of(LocalTime.of(0,0), LocalTime.of(0,0));

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    public static ScheduleTime of(LocalTime startTime, LocalTime endTime) {
        requiredNotNull(startTime, endTime);
        return new ScheduleTime(startTime, endTime);
    }

    private static void requiredNotNull(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new InvalidScheduleTimeException("일정의 시작, 종료 시간은 null일 수 없음");
        }
    }

    /**
     * 일정의 디폴트 시간을 반환합니다. 0시 0분부터 0시 0분까지의 일정으로 지정됩니다.
     * @return 일정의 디폴트 시간
     */
    public static ScheduleTime defaultTime() {
        return DEFAULT_TIME;
    }

    private ScheduleTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
