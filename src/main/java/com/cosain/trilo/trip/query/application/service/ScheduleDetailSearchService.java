package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.dto.ScheduleResult;
import com.cosain.trilo.trip.query.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.query.application.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
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
