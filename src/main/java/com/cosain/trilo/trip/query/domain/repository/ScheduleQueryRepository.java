package com.cosain.trilo.trip.query.domain.repository;

import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<ScheduleDto> findScheduleDetailByScheduleId(Long scheduleId);

    Slice<ScheduleDto> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable);
}
