package com.cosain.trilo.trip.query.presentation.trip.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TripPageResponse {

    private final boolean hasNext;
    private final List<TripDetailResponse> trips;

    public static TripPageResponse of(final List<TripDetailResponse> trips, final boolean hasNext){
        return new TripPageResponse(trips, hasNext);
    }

    private TripPageResponse(final List<TripDetailResponse> trips, final boolean hasNext){
        this.hasNext = hasNext;
        this.trips = trips;
    }


}
