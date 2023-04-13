package com.cosain.trilo.trip.command.presentation.trip.dto;

import lombok.Getter;

@Getter
public class TripCreateResponse {

    private Long tripId;
    public static TripCreateResponse from(Long tripId) {
        return new TripCreateResponse(tripId);
    }
    private TripCreateResponse(Long tripId) {
        this.tripId = tripId;
    }

}
