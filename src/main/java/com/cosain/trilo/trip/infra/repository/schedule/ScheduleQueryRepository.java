package com.cosain.trilo.trip.infra.repository.schedule;

import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<ScheduleDetail> findScheduleDetailByScheduleId(Long scheduleId);

    Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable);
}
