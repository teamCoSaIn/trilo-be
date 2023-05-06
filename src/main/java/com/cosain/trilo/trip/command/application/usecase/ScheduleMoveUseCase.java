package com.cosain.trilo.trip.command.application.usecase;

import com.cosain.trilo.trip.command.application.command.ScheduleMoveCommand;

public interface ScheduleMoveUseCase {

    void moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand);
}
