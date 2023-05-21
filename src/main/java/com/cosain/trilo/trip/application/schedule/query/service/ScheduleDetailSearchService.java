package com.cosain.trilo.trip.application.schedule.query.service;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.query.service.dto.ScheduleResult;
import com.cosain.trilo.trip.domain.dto.ScheduleDto;
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
    public ScheduleResult searchScheduleDetail(Long scheduleId) {

        ScheduleDto scheduleDto = findSchedule(scheduleId);

        return ScheduleResult.from(scheduleDto);
    }

    private ScheduleDto findSchedule(Long scheduleId) {
        return scheduleQueryRepository.findScheduleDetailByScheduleId(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

}
