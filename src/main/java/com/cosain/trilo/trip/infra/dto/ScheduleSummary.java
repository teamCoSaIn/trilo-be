package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleSummary {
    private Long scheduleId;
    private String title;
    private String placeName;
    private String placeId;
    private Coordinate coordinate;

    @QueryProjection
    public ScheduleSummary(Long scheduleId, String title, String placeName,String placeId, double latitude, double longitude) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.placeName = placeName;
        this.placeId = placeId;
        this.coordinate = Coordinate.from(latitude, longitude);
    }
}
