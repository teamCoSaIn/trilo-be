package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetailSearchService;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SingleScheduleQueryController {

    private final ScheduleDetailSearchService scheduleDetailSearchService;

    @GetMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDetail findSingleSchedule(@PathVariable Long scheduleId) {
        return scheduleDetailSearchService.searchScheduleDetail(scheduleId);
    }
}
