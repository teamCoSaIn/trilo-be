package com.cosain.trilo.trip.presentation.day.query;

import com.cosain.trilo.trip.application.day.query.usecase.DaySearchUseCase;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
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
public class SingleDayQueryController {

    private final DaySearchUseCase daySearchUseCase;

    @GetMapping("/api/days/{dayId}")
    @ResponseStatus(HttpStatus.OK)
    public DayScheduleDetail findSingleDay(@PathVariable Long dayId) {
        return daySearchUseCase.searchDeySchedule(dayId);
    }
}
