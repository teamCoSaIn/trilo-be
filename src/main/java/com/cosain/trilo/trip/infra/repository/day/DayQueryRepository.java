package com.cosain.trilo.trip.infra.repository.day;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;

import java.util.List;
import java.util.Optional;

public interface DayQueryRepository {
    Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId);

    List<DayScheduleDetail> findDayScheduleListByTripId(Long tripId);
}
