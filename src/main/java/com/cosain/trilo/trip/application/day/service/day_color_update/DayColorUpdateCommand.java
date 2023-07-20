package com.cosain.trilo.trip.application.day.service.day_color_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.DayColor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"dayId", "requestTripperId", "dayColor"})
public class DayColorUpdateCommand {

    private final long dayId;
    private final long requestTripperId;
    private final DayColor dayColor;

    public static DayColorUpdateCommand of(long dayId, long requestTripperId, String rawColorName) {
        List<CustomException> exceptions = new ArrayList<>();

        DayColor dayColor = makeDayColor(rawColorName, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new DayColorUpdateCommand(dayId, requestTripperId, dayColor);
    }

    private static DayColor makeDayColor(String rawColorName, List<CustomException> exceptions) {
        try {
            return DayColor.of(rawColorName);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    private DayColorUpdateCommand(long dayId, long requestTripperId, DayColor dayColor) {
        this.dayId = dayId;
        this.requestTripperId = requestTripperId;
        this.dayColor = dayColor;
    }
}
