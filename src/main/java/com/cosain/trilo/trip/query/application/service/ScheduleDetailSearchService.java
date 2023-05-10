package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.query.application.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.query.presentation.schedule.dto.ScheduleDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDetailSearchService implements ScheduleDetailSearchUseCase {

    private final ScheduleQueryRepository scheduleQueryRepository;

    @Override
    public ScheduleDetailResponse searchScheduleDetail(Long scheduleId) {

        ScheduleDetail scheduleDetail = findScheduleDetail(scheduleId);

        return ScheduleDetailResponse.of(scheduleDetail.getScheduleId(), scheduleDetail.getDayId(), scheduleDetail.getTitle(), scheduleDetail.getPlaceName(), scheduleDetail.getLatitude(), scheduleDetail.getLongitude(), scheduleDetail.getOrder(), scheduleDetail.getContent());
    }

    private ScheduleDetail findScheduleDetail(Long scheduleId) {
        return scheduleQueryRepository.findScheduleDetailByScheduleId(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

}
