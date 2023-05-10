package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.application.dto.ScheduleResult;

public interface ScheduleDetailSearchUseCase {
    ScheduleResult searchScheduleDetail(Long scheduleId);
}
