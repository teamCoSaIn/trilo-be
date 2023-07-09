package com.cosain.trilo.trip.application.trip.dto.factory;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.dto.TripCreateCommand;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TripCreateCommandFactory {

    public TripCreateCommand createCommand(String rawTitle) {
        List<CustomException> exceptions = new ArrayList<>();
        TripTitle tripTitle = makeTripTitle(rawTitle, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripCreateCommand(tripTitle);
    }

    private TripTitle makeTripTitle(String rawTitle, List<CustomException> exceptions) {
        try {
            return TripTitle.of(rawTitle);
        } catch (CustomException e) {
            exceptions.add(e);
        }
        return null;
    }
}
