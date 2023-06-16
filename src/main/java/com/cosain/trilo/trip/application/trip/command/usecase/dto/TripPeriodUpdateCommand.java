package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.Getter;

@Getter
public class TripPeriodUpdateCommand {

    private TripPeriod tripPeriod;

    public TripPeriodUpdateCommand(TripPeriod tripPeriod) {
        this.tripPeriod = tripPeriod;
    }
}
