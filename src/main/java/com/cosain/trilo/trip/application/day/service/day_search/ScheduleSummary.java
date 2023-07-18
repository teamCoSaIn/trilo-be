package com.cosain.trilo.trip.application.day.service.day_search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleSummary {
    private Long scheduleId;
    private String title;
    private String placeName;
    private String placeId;
    private CoordinateDto coordinate;

    @QueryProjection
    public ScheduleSummary(Long scheduleId, String title, String placeName,String placeId, double latitude, double longitude) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.placeName = placeName;
        this.placeId = placeId;
        this.coordinate = new CoordinateDto(latitude, longitude);
    }

    @Getter
    public static class CoordinateDto {

        private final double latitude;
        private final double longitude;

        public CoordinateDto(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
