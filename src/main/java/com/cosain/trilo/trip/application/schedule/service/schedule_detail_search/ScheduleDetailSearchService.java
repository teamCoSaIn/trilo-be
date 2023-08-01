package com.cosain.trilo.trip.application.schedule.service.schedule_detail_search;

import com.cosain.trilo.common.exception.schedule.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDetailSearchService {

    private final ScheduleQueryDAO scheduleQueryDAO;

    public ScheduleDetail searchScheduleDetail(Long scheduleId) {
        return findSchedule(scheduleId);
    }

    private ScheduleDetail findSchedule(Long scheduleId) {
        return scheduleQueryDAO.findScheduleDetailById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

}
