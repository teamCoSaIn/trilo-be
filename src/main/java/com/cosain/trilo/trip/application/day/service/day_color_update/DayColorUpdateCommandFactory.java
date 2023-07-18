package com.cosain.trilo.trip.application.day.service.day_color_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.DayColor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DayColorUpdateCommandFactory {

    public DayColorUpdateCommand createCommand(String rawColorName) {
        List<CustomException> exceptions = new ArrayList<>();

        DayColor dayColor = makeDayColor(rawColorName, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new DayColorUpdateCommand(dayColor);
    }

    private DayColor makeDayColor(String rawColorName, List<CustomException> exceptions) {
        try {
            return DayColor.of(rawColorName);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }
}
