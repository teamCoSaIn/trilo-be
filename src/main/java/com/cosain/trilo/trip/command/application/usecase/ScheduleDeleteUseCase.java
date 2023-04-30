package com.cosain.trilo.trip.command.application.usecase;

public interface ScheduleDeleteUseCase {

    void deleteSchedule(Long scheduleId, Long deleteTripperId);
}
