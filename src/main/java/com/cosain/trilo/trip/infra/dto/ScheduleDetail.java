package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ScheduleDetail {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private Coordinate coordinate;
    private long order;
    private String content;
    private ScheduleTime scheduleTime;

    @QueryProjection
    public ScheduleDetail(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content, LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.coordinate = Coordinate.from(latitude, longitude);
        this.order = order;
        this.content = content;
        this.scheduleTime = ScheduleTime.of(startTime, endTime);
    }
}
