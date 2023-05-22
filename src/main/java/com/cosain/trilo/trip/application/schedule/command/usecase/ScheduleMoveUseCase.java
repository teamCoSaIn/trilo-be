package com.cosain.trilo.trip.application.schedule.command.usecase;

import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;

public interface ScheduleMoveUseCase {

    ScheduleMoveResult moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand);
}
