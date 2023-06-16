package com.cosain.trilo.trip.application.trip.command.usecase.dto.factory;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripPeriodUpdateCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TripPeriodUpdateCommandFactory {

    public TripPeriodUpdateCommand createCommand(LocalDate startDate, LocalDate endDate) {
        return null;
    }
}
