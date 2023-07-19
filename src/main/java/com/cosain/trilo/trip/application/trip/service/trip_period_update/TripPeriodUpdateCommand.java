package com.cosain.trilo.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"tripId", "requestTripperId", "tripPeriod"})
public class TripPeriodUpdateCommand {

    private final long tripId;
    private final long requestTripperId;
    private final TripPeriod tripPeriod;

    public static TripPeriodUpdateCommand of(long tripId, long tripperId, LocalDate startDate, LocalDate endDate) {
        List<CustomException> exceptions = new ArrayList<>();
        TripPeriod tripPeriod = makeTripPeriod(startDate, endDate, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripPeriodUpdateCommand(tripId, tripperId, tripPeriod);
    }

    private static TripPeriod makeTripPeriod(LocalDate startDate, LocalDate endDate, List<CustomException> exceptions) {
        try {
            return TripPeriod.of(startDate, endDate);
        } catch (CustomException e) {
            exceptions.add(e);
        }
        return null;
    }

    private TripPeriodUpdateCommand(long tripId, long requestTripperId, TripPeriod tripPeriod) {
        this.tripId = tripId;
        this.requestTripperId = requestTripperId;
        this.tripPeriod = tripPeriod;
    }
}
