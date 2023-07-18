package com.cosain.trilo.trip.application.trip.service.trip_create;

import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.Getter;

@Getter
public class TripCreateCommand {

    private TripTitle tripTitle;

    public TripCreateCommand(TripTitle tripTitle) {
        this.tripTitle = tripTitle;
    }
}
