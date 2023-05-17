package com.cosain.trilo.trip.query.infra.repository.schedule.jpa;

import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ScheduleQueryJpaRepositoryCustom {
    Optional<ScheduleDetail> findScheduleDetailById(Long id);
    Slice<ScheduleDetail> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable);
}
