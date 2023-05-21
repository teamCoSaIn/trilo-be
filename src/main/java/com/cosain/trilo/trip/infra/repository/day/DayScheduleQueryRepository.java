package com.cosain.trilo.trip.infra.repository.day;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;

import java.util.Optional;

public interface DayScheduleQueryRepository {
    Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId);
}
