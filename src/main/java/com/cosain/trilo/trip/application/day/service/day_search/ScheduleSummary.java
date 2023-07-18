package com.cosain.trilo.trip.application.day.service.day_search;

import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.Coordinate;
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
