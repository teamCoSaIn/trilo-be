package com.cosain.trilo.trip.command.application.command;

import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import lombok.Getter;

@Getter
public class TripUpdateCommand {

    private String title;
    private TripPeriod tripPeriod;

    public static TripUpdateCommand of(String title, TripPeriod tripPeriod) {
        return new TripUpdateCommand(title, tripPeriod);
    }

    private TripUpdateCommand(String title, TripPeriod tripPeriod) {
        this.title = title;
        this.tripPeriod = tripPeriod;
    }
}
