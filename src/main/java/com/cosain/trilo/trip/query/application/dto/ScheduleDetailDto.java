package com.cosain.trilo.trip.query.application.dto;

import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.*;

@Getter
public class ScheduleDetailDto {
    private long scheduleId;
    private long dayId;
    private String title;
    private String placeName;
    private double latitude;
    private double longitude;
    private long order;
    private String content;

    @Builder(access = PRIVATE)
    private ScheduleDetailDto(long scheduleId, long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
        this.content = content;
    }

    public static ScheduleDetailDto from(ScheduleDetail scheduleDetail){
        return ScheduleDetailDto.builder()
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
}
