package com.cosain.trilo.trip.application.day.query.service;

import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.query.domain.repository.DayQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DaySearchService implements DaySearchUseCase {

    private final DayQueryRepository dayQueryRepository;

    @Override
    public DayScheduleDetail searchDeySchedule(Long dayId) {
        return findDayWithScheduleByDayId(dayId);
    }

    private DayScheduleDetail findDayWithScheduleByDayId(Long dayId){
        return dayQueryRepository.findDayWithSchedulesByDayId(dayId).orElseThrow(DayNotFoundException::new);
    }
}
