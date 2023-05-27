package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleDetail {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private Coordinate coordinate;
    private long order;
    private String content;

    @QueryProjection
    public ScheduleDetail(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.coordinate = Coordinate.from(latitude, longitude);
        this.order = order;
        this.content = content;
    }
}
