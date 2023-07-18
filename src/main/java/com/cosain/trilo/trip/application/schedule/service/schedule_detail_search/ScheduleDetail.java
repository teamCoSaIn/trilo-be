package com.cosain.trilo.trip.application.schedule.service.schedule_detail_search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ScheduleDetail {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private CoordinateDto coordinate;
    private long order;
    private String content;
    private ScheduleTimeDto scheduleTime;

    @QueryProjection
    public ScheduleDetail(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content, LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.coordinate = new CoordinateDto(latitude, longitude);
        this.order = order;
        this.content = content;
        this.scheduleTime = new ScheduleTimeDto(startTime, endTime);
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

    @Getter
    public static class ScheduleTimeDto {
        private final LocalTime startTime;
        private final LocalTime endTime;

        public ScheduleTimeDto(LocalTime startTime, LocalTime endTime){
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

}
