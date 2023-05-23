package com.cosain.trilo.trip.presentation.trip.query;

import com.cosain.trilo.trip.application.trip.query.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.presentation.trip.query.dto.response.TemporaryPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TripTemporaryStorageQueryController {

    private final TemporarySearchUseCase temporarySearchUseCase;

    @GetMapping("/api/trips/{tripId}/temporary-storage")
    @ResponseStatus(HttpStatus.OK)
    public TemporaryPageResponse findTripTemporaryStorage(@PathVariable Long tripId, Pageable pageable) {
        Slice<ScheduleDetail> scheduleDetails = temporarySearchUseCase.searchTemporary(tripId, pageable);
        return TemporaryPageResponse.from(scheduleDetails);
    }
}
