package com.cosain.trilo.trip.query.presentation.schedule.dto;

import com.cosain.trilo.trip.query.application.dto.ScheduleDetailDto;
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

    public static ScheduleDetailResponse from(ScheduleDetailDto scheduleDetailDto){
        return ScheduleDetailResponse.builder()
                .scheduleId(scheduleDetailDto.getScheduleId())
                .dayId(scheduleDetailDto.getDayId())
                .title(scheduleDetailDto.getTitle())
                .placeName(scheduleDetailDto.getPlaceName())
                .latitude(scheduleDetailDto.getLatitude())
                .longitude(scheduleDetailDto.getLongitude())
                .order(scheduleDetailDto.getOrder())
                .content(scheduleDetailDto.getContent())
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
