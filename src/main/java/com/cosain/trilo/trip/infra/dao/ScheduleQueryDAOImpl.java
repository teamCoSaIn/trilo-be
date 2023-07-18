package com.cosain.trilo.trip.infra.dao;

import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.infra.dao.querydsl.QuerydslScheduleQueryRepository;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleQueryDAOImpl implements ScheduleQueryDAO {

    private final QuerydslScheduleQueryRepository querydslScheduleQueryRepository;

    @Override
    public Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId) {
        return querydslScheduleQueryRepository.findScheduleDetailById(scheduleId);
    }

    @Override
    public Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {
        return querydslScheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition, pageable);
    }

    @Override
    public boolean existById(Long scheduleId) {
        return querydslScheduleQueryRepository.existById(scheduleId);
    }
}
