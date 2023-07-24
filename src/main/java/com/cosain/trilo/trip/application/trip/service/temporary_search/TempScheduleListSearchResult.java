package com.cosain.trilo.trip.application.trip.service.temporary_search;

import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import lombok.Getter;

import java.util.List;

@Getter
public class TempScheduleListSearchResult {

    private final boolean hasNext;
    private final List<ScheduleSummary> tempSchedules;

    public static TempScheduleListSearchResult of(boolean hasNext, List<ScheduleSummary> tempSchedules) {
        return new TempScheduleListSearchResult(hasNext, tempSchedules);
    }

    private TempScheduleListSearchResult(boolean hasNext, List<ScheduleSummary> tempSchedules) {
        this.hasNext = hasNext;
        this.tempSchedules = tempSchedules;
    }
}
