package com.cosain.trilo.trip.query.domain.repository;

import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<ScheduleDto> findScheduleDetailByScheduleId(Long scheduleId);
}
