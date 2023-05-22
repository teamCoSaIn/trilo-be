package com.cosain.trilo.trip.application.trip.query.usecase.dto;

import com.cosain.trilo.trip.application.schedule.query.usecase.dto.ScheduleResult;
import lombok.Getter;

import java.util.List;

@Getter
public class TemporaryPageResult {
    private boolean hasNext;
    private final List<ScheduleResult> scheduleResults;

    public static TemporaryPageResult of(final List<ScheduleResult> scheduleResults, final boolean hasNext) {
        return new TemporaryPageResult(scheduleResults, hasNext);
    }

    private TemporaryPageResult(final List<ScheduleResult> scheduleResults, final boolean hasNext){
        this.hasNext = hasNext;
        this.scheduleResults = scheduleResults;
    }
}
