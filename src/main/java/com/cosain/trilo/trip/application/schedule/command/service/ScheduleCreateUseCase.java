package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleCreateCommand;

public interface ScheduleCreateUseCase {

    Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand);
}
