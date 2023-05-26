package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripUpdateCommand {

    private TripTitle tripTitle;
    private TripPeriod tripPeriod;

    public static TripUpdateCommand of(String rawTitle, LocalDate startDate, LocalDate endDate) {
        TripTitle tripTitle = TripTitle.of(rawTitle);
        return new TripUpdateCommand(tripTitle, TripPeriod.of(startDate, endDate));
    }

    private TripUpdateCommand(TripTitle tripTitle, TripPeriod tripPeriod) {
        this.tripTitle = tripTitle;
        this.tripPeriod = tripPeriod;
    }
}
