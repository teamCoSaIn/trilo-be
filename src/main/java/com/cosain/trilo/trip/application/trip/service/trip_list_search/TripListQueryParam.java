package com.cosain.trilo.trip.application.trip.service.trip_list_search;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"tripperId", "tripId","pageSize"})
public class TripListQueryParam {

    private final Long tripperId;
    private final Long tripId;
    private final int pageSize;

    public static TripListQueryParam of(Long tripperId, Long tripId, Integer pageSize) {
        return new TripListQueryParam(tripperId, tripId, pageSize);
    }

    private TripListQueryParam(Long tripperId, Long tripId, int pageSize) {
        this.tripperId = tripperId;
        this.tripId = tripId;
        this.pageSize = pageSize;
    }
}
