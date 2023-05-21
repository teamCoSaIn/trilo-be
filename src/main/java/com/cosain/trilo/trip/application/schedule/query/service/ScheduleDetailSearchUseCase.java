package com.cosain.trilo.trip.application.schedule.query.service;

import com.cosain.trilo.trip.application.schedule.query.service.dto.ScheduleResult;

public interface ScheduleDetailSearchUseCase {
    ScheduleResult searchScheduleDetail(Long scheduleId);
}
