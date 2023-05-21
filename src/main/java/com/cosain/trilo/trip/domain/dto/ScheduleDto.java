package com.cosain.trilo.trip.domain.dto;

import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleDto {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private double latitude;
    private double longitude;
    private long order;
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleDto(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
        this.content = content;
    }

    public static ScheduleDto from(ScheduleDetail scheduleDetail){
        return ScheduleDto.builder()
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
