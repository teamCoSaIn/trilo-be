package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.presentation.schedule.dto.ScheduleDetailResponse;

public interface ScheduleDetailSearchUseCase {
    ScheduleDetailResponse searchScheduleDetail(Long scheduleId);
}
