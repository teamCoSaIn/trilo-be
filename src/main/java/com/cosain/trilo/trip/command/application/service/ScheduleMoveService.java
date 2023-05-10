package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleMoveCommand;
import com.cosain.trilo.trip.command.application.usecase.ScheduleMoveUseCase;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMoveService implements ScheduleMoveUseCase {

    @Override
    public void moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand) {
    }

}
