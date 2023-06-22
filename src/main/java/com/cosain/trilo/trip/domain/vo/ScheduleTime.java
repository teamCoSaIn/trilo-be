package com.cosain.trilo.trip.domain.vo;

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
        return new ScheduleTime(startTime, endTime);
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
