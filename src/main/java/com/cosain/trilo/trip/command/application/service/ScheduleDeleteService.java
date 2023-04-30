package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.usecase.ScheduleDeleteUseCase;
import org.springframework.stereotype.Service;

@Service
public class ScheduleDeleteService implements ScheduleDeleteUseCase {

    @Override
    public void deleteSchedule(Long scheduleId, Long deleteTripperId) {
    }
}
