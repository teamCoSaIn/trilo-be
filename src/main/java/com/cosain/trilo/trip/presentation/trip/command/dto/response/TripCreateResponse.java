package com.cosain.trilo.trip.presentation.trip.command.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripCreateResponse {

    private Long tripId;
    public static TripCreateResponse from(Long tripId) {
        return new TripCreateResponse(tripId);
    }
    private TripCreateResponse(Long tripId) {
        this.tripId = tripId;
    }

}
