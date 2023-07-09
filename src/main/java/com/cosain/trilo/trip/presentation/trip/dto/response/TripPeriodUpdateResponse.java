package com.cosain.trilo.trip.presentation.trip.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
public class TripPeriodUpdateResponse {

    private Long tripId;

    public TripPeriodUpdateResponse(Long tripId) {
        this.tripId = tripId;
    }
}
