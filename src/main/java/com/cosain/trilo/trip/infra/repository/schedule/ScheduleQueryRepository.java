package com.cosain.trilo.trip.infra.repository.schedule;

import com.cosain.trilo.trip.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<ScheduleDetail> findScheduleDetailByScheduleId(Long scheduleId);

    Slice<ScheduleDetail> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable);
}
