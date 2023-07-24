package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.Getter;

@Getter
public class TripListSearchRequest {

    private final Long tripperId;
    private final Long tripId;
    private final Integer size;

    public TripListSearchRequest(Long tripperId, Long tripId, Integer size) {
        this.tripperId = tripperId;
        this.tripId = tripId;
        this.size = size;
    }
}
