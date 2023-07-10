package com.cosain.trilo.trip.application.day.service;

import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.repository.day.DayQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DaySearchService{

    private final DayQueryRepository dayQueryRepository;

    public DayScheduleDetail searchDeySchedule(Long dayId) {
        return findDayWithScheduleByDayId(dayId);
    }

    private DayScheduleDetail findDayWithScheduleByDayId(Long dayId){
        return dayQueryRepository.findDayWithSchedulesByDayId(dayId).orElseThrow(DayNotFoundException::new);
    }

    public List<DayScheduleDetail> searchDaySchedules(Long tripId){
        return dayQueryRepository.findDayScheduleListByTripId(tripId);
    }
}