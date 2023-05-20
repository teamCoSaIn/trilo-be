package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;

public interface DaySearchUseCase {
    DayScheduleDetail searchDeySchedule(Long dayId);
}
