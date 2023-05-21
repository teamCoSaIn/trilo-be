package com.cosain.trilo.trip.application.schedule.command.service;

public interface ScheduleDeleteUseCase {

    void deleteSchedule(Long scheduleId, Long deleteTripperId);
}
