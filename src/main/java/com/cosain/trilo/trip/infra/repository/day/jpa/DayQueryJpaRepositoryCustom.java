package com.cosain.trilo.trip.infra.repository.day.jpa;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;

import java.util.Optional;

public interface DayQueryJpaRepositoryCustom {
    Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId);
}
