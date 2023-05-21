package com.cosain.trilo.trip.application.trip.command.service.dto;

import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripUpdateCommand {

    private String title;
    private TripPeriod tripPeriod;

    public static TripUpdateCommand of(String title, LocalDate startDate, LocalDate endDate) {
        return new TripUpdateCommand(title, TripPeriod.of(startDate, endDate));
    }

    private TripUpdateCommand(String title, TripPeriod tripPeriod) {
        this.title = title;
        this.tripPeriod = tripPeriod;
    }
}
