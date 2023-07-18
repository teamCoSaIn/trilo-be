package com.cosain.trilo.trip.application.schedule.service.schedule_detail_search;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDetailSearchService {

    private final ScheduleQueryRepository scheduleQueryRepository;

    public ScheduleDetail searchScheduleDetail(Long scheduleId) {
        return findSchedule(scheduleId);
    }

    private ScheduleDetail findSchedule(Long scheduleId) {
        return scheduleQueryRepository.findScheduleDetailById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

}
