package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleMoveTargetOrderException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleMoveCommandFactory {


    public ScheduleMoveCommand createCommand(Long targetDayId, Integer targetOrder) {
        List<CustomException> exceptions = new ArrayList<>();
        validateTargetOrder(targetOrder, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new ScheduleMoveCommand(targetDayId, targetOrder);
    }

    private void validateTargetOrder(Integer targetOrder, List<CustomException> exceptions) {
        if (targetOrder == null || targetOrder < 0) {
            exceptions.add(new InvalidScheduleMoveTargetOrderException("순서값이 null 또는 음수"));
        }
    }

}
