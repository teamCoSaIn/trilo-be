package com.cosain.trilo.trip.application.trip.service.trip_title_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"tripId", "requestTripperId", "tripTitle"})
public class TripTitleUpdateCommand {

    private final long tripId;
    private final long requestTripperId;
    private final TripTitle tripTitle;

    public static TripTitleUpdateCommand of(long tripId, long requestTripperId, String rawTitle) {
        List<CustomException> exceptions = new ArrayList<>();
        TripTitle tripTitle = makeTripTitle(rawTitle, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripTitleUpdateCommand(tripId, requestTripperId, tripTitle);
    }

    private static TripTitle makeTripTitle(String rawTitle, List<CustomException> exceptions) {
        try {
            return TripTitle.of(rawTitle);
        } catch (CustomException e) {
            exceptions.add(e);
        }
        return null;
    }

    private TripTitleUpdateCommand(long tripId, long requestTripperId, TripTitle tripTitle) {
        this.tripId = tripId;
        this.requestTripperId = requestTripperId;
        this.tripTitle = tripTitle;
    }
}
