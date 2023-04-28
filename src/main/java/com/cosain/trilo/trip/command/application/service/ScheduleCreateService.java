package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import com.cosain.trilo.trip.command.application.usecase.ScheduleCreateUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleCreateService implements ScheduleCreateUseCase {

    @Override
    @Transactional
    public Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand) {
        return null;
    }

}
