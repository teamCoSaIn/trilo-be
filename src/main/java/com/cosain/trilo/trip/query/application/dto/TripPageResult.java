package com.cosain.trilo.trip.query.application.dto;

import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class TripPageResult {
    private final boolean hasNext;
    private final List<TripResult> trips;

    public static TripPageResult of(final List<TripResult> trips, final boolean hasNext){
        return new TripPageResult(trips, hasNext);
    }

    private TripPageResult(final List<TripResult> trips, final boolean hasNext){
        this.hasNext = hasNext;
        this.trips = trips;
    }
}
