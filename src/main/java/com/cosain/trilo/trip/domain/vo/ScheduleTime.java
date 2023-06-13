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

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    public static ScheduleTime of(LocalTime startTime, LocalTime endTime) {
        return new ScheduleTime(startTime, endTime);
    }

    private ScheduleTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
