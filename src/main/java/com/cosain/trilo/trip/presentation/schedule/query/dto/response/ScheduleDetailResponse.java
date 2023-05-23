package com.cosain.trilo.trip.presentation.schedule.query.dto.response;

import com.cosain.trilo.trip.application.schedule.query.usecase.dto.ScheduleResult;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleDetailResponse {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private double latitude;
    private double longitude;
    private long order;
    private String content;

    public static ScheduleDetailResponse from(ScheduleDetail scheduleDetail){
        return ScheduleDetailResponse.builder()
                .scheduleId(scheduleDetail.getScheduleId())
                .dayId(scheduleDetail.getDayId())
                .title(scheduleDetail.getTitle())
                .placeName(scheduleDetail.getPlaceName())
                .latitude(scheduleDetail.getLatitude())
                .longitude(scheduleDetail.getLongitude())
                .order(scheduleDetail.getOrder())
                .content(scheduleDetail.getContent())
                .build();
    }

    @Builder
    private ScheduleDetailResponse(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
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
