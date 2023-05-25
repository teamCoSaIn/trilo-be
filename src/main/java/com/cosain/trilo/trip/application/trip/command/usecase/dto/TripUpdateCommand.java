package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.Getter;

@Getter
public class TripUpdateCommand {

    private TripTitle tripTitle;
    private TripPeriod tripPeriod;

    public TripUpdateCommand(TripTitle tripTitle, TripPeriod tripPeriod) {
        this.tripTitle = tripTitle;
        this.tripPeriod = tripPeriod;
    }
}
