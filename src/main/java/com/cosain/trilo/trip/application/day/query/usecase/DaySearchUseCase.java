package com.cosain.trilo.trip.application.day.query.usecase;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;

import java.util.List;

public interface DaySearchUseCase {
    DayScheduleDetail searchDeySchedule(Long dayId);

    List<DayScheduleDetail> findDaysWithSchedulesByTripId(Long tripId);
}
