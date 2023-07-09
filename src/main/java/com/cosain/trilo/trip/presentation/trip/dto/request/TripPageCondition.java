package com.cosain.trilo.trip.presentation.trip.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TripPageCondition {

    @NotBlank
    private Long tripperId;

    private Long tripId;

    public TripPageCondition(Long tripperId, Long tripId){
        this.tripperId = tripperId;
        this.tripId = tripId;
    }
}
