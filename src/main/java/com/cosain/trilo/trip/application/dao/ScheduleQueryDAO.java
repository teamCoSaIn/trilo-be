package com.cosain.trilo.trip.application.dao;

import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ScheduleQueryDAO {

    Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId);
    Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable);
    boolean existById(Long scheduleId);
}
