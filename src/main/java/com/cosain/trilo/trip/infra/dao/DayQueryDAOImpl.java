package com.cosain.trilo.trip.infra.dao;

import com.cosain.trilo.trip.application.dao.DayQueryDAO;
import com.cosain.trilo.trip.application.day.service.day_search.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dao.querydsl.QuerydslDayQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DayQueryDAOImpl implements DayQueryDAO {

    private final QuerydslDayQueryRepository querydslDayQueryRepository;

    @Override
    public Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId) {
        return querydslDayQueryRepository.findDayWithSchedulesByDayId(dayId);
    }

    @Override
    public List<DayScheduleDetail> findDayScheduleListByTripId(Long tripId) {
        return querydslDayQueryRepository.findDayScheduleListByTripId(tripId);
    }
}
