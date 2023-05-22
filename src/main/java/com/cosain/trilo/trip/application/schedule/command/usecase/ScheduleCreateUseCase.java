package com.cosain.trilo.trip.application.schedule.command.usecase;

import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleCreateCommand;

public interface ScheduleCreateUseCase {

    Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand);
}
