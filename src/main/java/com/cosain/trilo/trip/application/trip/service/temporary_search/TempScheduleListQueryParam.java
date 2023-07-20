package com.cosain.trilo.trip.application.trip.service.temporary_search;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"tripId", "scheduleId","pageSize"})
public class TempScheduleListQueryParam {

    private final long tripId;
    private final Long scheduleId;
    private final int pageSize;

    public static TempScheduleListQueryParam of(long tripId, Long scheduleId, Integer pageSize) {
        return new TempScheduleListQueryParam(tripId, scheduleId, pageSize);
    }

    public TempScheduleListQueryParam(long tripId, Long scheduleId, int pageSize) {
        this.tripId = tripId;
        this.scheduleId = scheduleId;
        this.pageSize = pageSize;
    }
}
