package com.cosain.trilo.trip.query.presentation.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleDetailResponse {
    private long scheduleId;
    private long dayId;
    private String title;
    private String placeName;
    private double latitude;
    private double longitude;
    private long order;
    private String content;

    public static ScheduleDetailResponse of(long scheduleId, long dayId, String title, String placeName, double latitude, double longitude, long order, String content){
        return ScheduleDetailResponse.builder()
                .scheduleId(scheduleId)
                .dayId(dayId)
                .title(title)
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .order(order)
                .content(content)
                .build();
    }

    @Builder
    private ScheduleDetailResponse(long scheduleId, long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
        this.content = content;
    }
}
