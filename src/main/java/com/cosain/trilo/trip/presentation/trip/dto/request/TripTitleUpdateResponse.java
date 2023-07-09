package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.Getter;

@Getter
public class TripTitleUpdateResponse {

    private Long tripId;

    public TripTitleUpdateResponse(Long tripId) {
        this.tripId = tripId;
    }
}
