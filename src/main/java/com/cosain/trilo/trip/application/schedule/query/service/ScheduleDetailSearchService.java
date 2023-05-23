package com.cosain.trilo.trip.application.schedule.query.service;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.query.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDetailSearchService implements ScheduleDetailSearchUseCase {

    private final ScheduleQueryRepository scheduleQueryRepository;

    @Override
    public ScheduleDetail searchScheduleDetail(Long scheduleId) {
        return findSchedule(scheduleId);
    }

    private ScheduleDetail findSchedule(Long scheduleId) {
        return scheduleQueryRepository.findScheduleDetailByScheduleId(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

}
