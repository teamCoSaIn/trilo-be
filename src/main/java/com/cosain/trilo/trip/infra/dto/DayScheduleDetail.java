package com.cosain.trilo.trip.infra.dto;

import com.cosain.trilo.trip.domain.vo.DayColor;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

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
        this.dayColor = DayColorDto.of(dayColor.name(), dayColor.getValue());
        this.schedules = scheduleSummaries;
    }
}
