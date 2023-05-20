package com.cosain.trilo.trip.query.domain.repository;

import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;

import java.util.Optional;

public interface DayQueryRepository {
    Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId);
}
