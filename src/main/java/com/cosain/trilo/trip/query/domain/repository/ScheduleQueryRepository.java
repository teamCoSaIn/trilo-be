package com.cosain.trilo.trip.query.domain.repository;

import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<ScheduleDetail> findScheduleDetailByScheduleId(Long scheduleId);
}
