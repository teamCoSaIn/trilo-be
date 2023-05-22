package com.cosain.trilo.trip.application.schedule.query.usecase.dto;

import com.cosain.trilo.trip.domain.dto.ScheduleDto;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.*;

@Getter
public class ScheduleResult {
    private long scheduleId;
    private Long dayId;
    private String title;
    private String placeName;
    private double latitude;
    private double longitude;
    private long order;
    private String content;

    @Builder(access = PRIVATE)
    private ScheduleResult(long scheduleId, Long dayId, String title, String placeName, double latitude, double longitude, long order, String content) {
        this.scheduleId = scheduleId;
        this.dayId = dayId;
        this.title = title;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
        this.content = content;
    }

    public static ScheduleResult from(ScheduleDto scheduleDto){
        return ScheduleResult.builder()
                .scheduleId(scheduleDto.getScheduleId())
                .dayId(scheduleDto.getDayId())
                .title(scheduleDto.getTitle())
                .placeName(scheduleDto.getPlaceName())
                .latitude(scheduleDto.getLatitude())
                .longitude(scheduleDto.getLongitude())
                .order(scheduleDto.getOrder())
                .content(scheduleDto.getContent())
                .build();
    }
}
