package com.cosain.trilo.trip.application.schedule.query.usecase;

import com.cosain.trilo.trip.application.schedule.query.usecase.dto.ScheduleResult;

public interface ScheduleDetailSearchUseCase {
    ScheduleResult searchScheduleDetail(Long scheduleId);
}
