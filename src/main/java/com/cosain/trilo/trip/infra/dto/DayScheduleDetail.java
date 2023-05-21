package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DayScheduleDetail {
    private Long dayId;
    private Long tripId;
    private LocalDate date;
    private List<ScheduleSummary> scheduleSummaries;

    @QueryProjection
    public DayScheduleDetail(Long dayId, Long tripId, LocalDate date, List<ScheduleSummary> scheduleSummaries) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.date = date;
        this.scheduleSummaries = scheduleSummaries;
    }

    @Getter
    public static class ScheduleSummary{
        private Long scheduleId;
        private String title;
        private String placeName;
        private double latitude;
        private double longitude;

        @QueryProjection
        public ScheduleSummary(Long scheduleId, String title, String placeName, double latitude, double longitude) {
            this.scheduleId = scheduleId;
            this.title = title;
            this.placeName = placeName;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
