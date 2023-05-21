package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleMoveResult;

public interface ScheduleMoveUseCase {

    ScheduleMoveResult moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand);
}
