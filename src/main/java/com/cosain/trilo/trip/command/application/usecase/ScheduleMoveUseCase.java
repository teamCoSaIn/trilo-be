package com.cosain.trilo.trip.command.application.usecase;

import com.cosain.trilo.trip.command.application.command.ScheduleMoveCommand;
import com.cosain.trilo.trip.command.application.result.ScheduleMoveResult;

public interface ScheduleMoveUseCase {

    ScheduleMoveResult moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand);
}
