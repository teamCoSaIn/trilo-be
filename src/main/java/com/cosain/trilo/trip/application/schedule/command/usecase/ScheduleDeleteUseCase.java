package com.cosain.trilo.trip.application.schedule.command.usecase;

public interface ScheduleDeleteUseCase {

    void deleteSchedule(Long scheduleId, Long deleteTripperId);
}
