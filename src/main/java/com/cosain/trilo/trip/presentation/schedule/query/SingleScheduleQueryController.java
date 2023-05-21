package com.cosain.trilo.trip.presentation.schedule.query;

import com.cosain.trilo.trip.query.application.dto.ScheduleResult;
import com.cosain.trilo.trip.query.application.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.presentation.schedule.query.dto.response.ScheduleDetailResponse;
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

    private final ScheduleDetailSearchUseCase scheduleDetailSearchUseCase;

    @GetMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDetailResponse findSingleSchedule(@PathVariable Long scheduleId) {
        ScheduleResult scheduleResult = scheduleDetailSearchUseCase.searchScheduleDetail(scheduleId);
        return ScheduleDetailResponse.from(scheduleResult);
    }
}
