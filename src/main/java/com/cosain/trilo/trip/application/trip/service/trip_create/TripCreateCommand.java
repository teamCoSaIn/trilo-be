package com.cosain.trilo.trip.application.trip.service.trip_create;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"tripperId", "tripTitle"})
public class TripCreateCommand {

    private final long tripperId;
    private final TripTitle tripTitle;

    public static TripCreateCommand of(Long tripperId, String rawTitle) {
        List<CustomException> exceptions = new ArrayList<>();

        TripTitle tripTitle = makeTripTitle(rawTitle, exceptions);
        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripCreateCommand(tripperId, tripTitle);
    }

    private TripCreateCommand(long tripperId, TripTitle tripTitle) {
        this.tripperId = tripperId;
        this.tripTitle = tripTitle;
    }

    private static TripTitle makeTripTitle(String rawTitle, List<CustomException> exceptions) {
        try {
            return TripTitle.of(rawTitle);
        } catch (CustomException e) {
            exceptions.add(e);
        }
        return null;
    }
}
