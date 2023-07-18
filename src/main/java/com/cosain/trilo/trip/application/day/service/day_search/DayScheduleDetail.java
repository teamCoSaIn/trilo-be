package com.cosain.trilo.trip.application.day.service.day_search;

import com.cosain.trilo.trip.domain.vo.DayColor;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DayScheduleDetail {
    private Long dayId;
    private Long tripId;
    private LocalDate date;
    private DayColorDto dayColor;
    private List<ScheduleSummary> schedules;

    @QueryProjection
    public DayScheduleDetail(Long dayId, Long tripId, LocalDate date, DayColor dayColor, List<ScheduleSummary> scheduleSummaries) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.date = date;
        this.dayColor = new DayColorDto(dayColor.name(), dayColor.getValue());
        this.schedules = scheduleSummaries;
    }

    @Getter
    public static class DayColorDto {

        private final String name;
        private final String code;

        public DayColorDto(String name, String code) {
            this.name = name;
            this.code = code;
        }

    }
}
