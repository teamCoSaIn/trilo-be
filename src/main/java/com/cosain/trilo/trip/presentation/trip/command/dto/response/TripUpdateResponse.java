package com.cosain.trilo.trip.presentation.trip.command.dto.response;

import lombok.Getter;

@Getter
public class TripUpdateResponse {

    private Long updatedTripId;

    public static TripUpdateResponse from(Long updatedTripId){
        return new TripUpdateResponse(updatedTripId);
    }

    private TripUpdateResponse(Long tripId){
        this.updatedTripId = tripId;
    }

}
