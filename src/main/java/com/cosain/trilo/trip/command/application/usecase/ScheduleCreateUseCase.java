package com.cosain.trilo.trip.command.application.usecase;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;

public interface ScheduleCreateUseCase {

    Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand);
}
