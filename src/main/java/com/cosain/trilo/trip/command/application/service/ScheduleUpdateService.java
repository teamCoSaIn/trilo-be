package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;
import com.cosain.trilo.trip.command.application.usecase.ScheduleUpdateUseCase;
import org.springframework.stereotype.Service;

@Service
public class ScheduleUpdateService implements ScheduleUpdateUseCase {

    @Override
    public Long updateSchedule(Long tripperId, Long scheduleId, ScheduleUpdateCommand scheduleUpdateCommand) {
        return null;
    }

}
