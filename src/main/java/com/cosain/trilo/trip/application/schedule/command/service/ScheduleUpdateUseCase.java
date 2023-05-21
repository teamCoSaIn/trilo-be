package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleUpdateCommand;

public interface ScheduleUpdateUseCase {
    Long updateSchedule( Long scheduleId,Long tripperId,ScheduleUpdateCommand scheduleUpdateCommand);
}
