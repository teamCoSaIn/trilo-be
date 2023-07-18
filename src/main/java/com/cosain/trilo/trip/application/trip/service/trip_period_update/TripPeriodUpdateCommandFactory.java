package com.cosain.trilo.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TripPeriodUpdateCommandFactory {

    public TripPeriodUpdateCommand createCommand(LocalDate startDate, LocalDate endDate) {
        List<CustomException> exceptions = new ArrayList<>();
        TripPeriod tripPeriod = makeTripPeriod(startDate, endDate, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripPeriodUpdateCommand(tripPeriod);
    }

    private TripPeriod makeTripPeriod(LocalDate startDate, LocalDate endDate, List<CustomException> exceptions) {
        try {
            return TripPeriod.of(startDate, endDate);
        } catch (CustomException e) {
            exceptions.add(e);
        }
        return null;
    }
}
