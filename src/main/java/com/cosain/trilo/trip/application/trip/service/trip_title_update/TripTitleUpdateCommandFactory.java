package com.cosain.trilo.trip.application.trip.service.trip_title_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TripTitleUpdateCommandFactory {

    public TripTitleUpdateCommand createCommand(String rawTitle) {
        List<CustomException> exceptions = new ArrayList<>();
        TripTitle tripTitle = makeTripTitle(rawTitle, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new TripTitleUpdateCommand(tripTitle);
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
