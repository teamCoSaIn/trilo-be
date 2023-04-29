package com.cosain.trilo.trip.command.application.usecase;

import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;

public interface ScheduleUpdateUseCase {
    Long updateSchedule(Long tripperId, Long scheduleId, ScheduleUpdateCommand scheduleUpdateCommand);
}
