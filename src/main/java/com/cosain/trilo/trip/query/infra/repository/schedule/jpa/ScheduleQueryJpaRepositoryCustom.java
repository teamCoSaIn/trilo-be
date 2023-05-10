package com.cosain.trilo.trip.query.infra.repository.schedule.jpa;

import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;

import java.util.Optional;

public interface ScheduleQueryJpaRepositoryCustom {
    Optional<ScheduleDetail> findScheduleDetailById(Long id);
}
