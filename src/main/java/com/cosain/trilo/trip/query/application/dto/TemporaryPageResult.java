package com.cosain.trilo.trip.query.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TemporaryPageResult {
    private boolean hasNext;
    private final List<ScheduleResult> scheduleResults;

    public static TemporaryPageResult of(final List<ScheduleResult> scheduleResults, final boolean hasNext) {
        return new TemporaryPageResult(scheduleResults, hasNext);
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TemporaryPageResult(final List<ScheduleResult> scheduleResults, final boolean hasNext){
        this.hasNext = hasNext;
        this.scheduleResults = scheduleResults;
    }
}
