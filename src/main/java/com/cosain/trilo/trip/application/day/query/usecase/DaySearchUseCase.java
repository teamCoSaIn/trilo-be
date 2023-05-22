package com.cosain.trilo.trip.application.day.query.usecase;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;

public interface DaySearchUseCase {
    DayScheduleDetail searchDeySchedule(Long dayId);
}
