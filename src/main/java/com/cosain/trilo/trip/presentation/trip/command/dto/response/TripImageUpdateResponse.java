package com.cosain.trilo.trip.presentation.trip.command.dto.response;

import lombok.Getter;

@Getter
public class TripImageUpdateResponse {

    private Long tripId;
    private String imagePath;

    public TripImageUpdateResponse(Long tripId, String imagePath) {
        this.tripId = tripId;
        this.imagePath = imagePath;
    }
}
