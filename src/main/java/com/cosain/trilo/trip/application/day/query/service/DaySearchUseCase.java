package com.cosain.trilo.trip.application.day.query.service;

import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;

public interface DaySearchUseCase {
    DayScheduleDetail searchDeySchedule(Long dayId);
}
