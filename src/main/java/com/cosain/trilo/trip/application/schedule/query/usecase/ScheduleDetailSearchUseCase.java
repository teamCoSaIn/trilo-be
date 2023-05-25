package com.cosain.trilo.trip.application.schedule.query.usecase;

import com.cosain.trilo.trip.infra.dto.ScheduleDetail;

public interface ScheduleDetailSearchUseCase {
    ScheduleDetail searchScheduleDetail(Long scheduleId);
}
