package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleMoveTargetOrderException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"scheduleId", "requestTripperId", "targetDayId", "targetOrder"})
public class ScheduleMoveCommand {

    private final long scheduleId;
    private final long requestTripperId;
    private final Long targetDayId;
    private final int targetOrder;

    public static ScheduleMoveCommand of(long scheduleId, long requestTripperId, Long targetDayId, Integer targetOrder) {
        List<CustomException> exceptions = new ArrayList<>();
        validateTargetOrder(targetOrder, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new ScheduleMoveCommand(scheduleId, requestTripperId, targetDayId, targetOrder);
    }

    private static void validateTargetOrder(Integer targetOrder, List<CustomException> exceptions) {
        if (targetOrder == null || targetOrder < 0) {
            exceptions.add(new InvalidScheduleMoveTargetOrderException("순서값이 null 또는 음수"));
        }
    }

    private ScheduleMoveCommand(long scheduleId, long requestTripperId, Long targetDayId, int targetOrder) {
        this.scheduleId = scheduleId;
        this.requestTripperId = requestTripperId;
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }
}
