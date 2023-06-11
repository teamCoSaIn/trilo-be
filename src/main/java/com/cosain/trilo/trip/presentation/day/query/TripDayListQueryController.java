package com.cosain.trilo.trip.presentation.day.query;

import com.cosain.trilo.trip.application.day.query.usecase.DaySearchUseCase;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.presentation.day.query.dto.DayListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripDayListQueryController {

    private final DaySearchUseCase daySearchUseCase;

    @GetMapping("/api/trips/{tripId}/days")
    @ResponseStatus(HttpStatus.OK)
    public DayListResponse findTripDayList(@PathVariable Long tripId) {
        List<DayScheduleDetail> dayScheduleDetails = daySearchUseCase.searchDaySchedules(tripId);
        return DayListResponse.of(dayScheduleDetails);
    }
}
