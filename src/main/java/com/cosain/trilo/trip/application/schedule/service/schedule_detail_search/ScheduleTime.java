package com.cosain.trilo.trip.application.schedule.service.schedule_detail_search;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ScheduleTime {
    private LocalTime startTime;
    private LocalTime endTime;

    private ScheduleTime(LocalTime startTime, LocalTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleTime of(LocalTime startTime, LocalTime endTime){
        return new ScheduleTime(startTime, endTime);
    }
}
