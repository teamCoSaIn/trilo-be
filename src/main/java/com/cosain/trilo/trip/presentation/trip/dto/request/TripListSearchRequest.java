package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.Getter;

@Getter
public class TripListSearchRequest {

    private final Long tripId;
    private final Integer size;

    public TripListSearchRequest(Long tripId, Integer size) {
        this.tripId = tripId;
        this.size = size;
    }
}
