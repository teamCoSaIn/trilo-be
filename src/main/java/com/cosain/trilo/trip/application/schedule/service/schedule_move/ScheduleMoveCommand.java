package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import lombok.Getter;

@Getter
public class ScheduleMoveCommand {

    private Long targetDayId;
    private int targetOrder;

    public ScheduleMoveCommand(Long targetDayId, int targetOrder) {
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }
}
