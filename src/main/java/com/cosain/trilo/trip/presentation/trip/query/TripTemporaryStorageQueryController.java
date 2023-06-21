package com.cosain.trilo.trip.presentation.trip.query;

import com.cosain.trilo.trip.application.trip.query.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TempSchedulePageCondition;
import com.cosain.trilo.trip.presentation.trip.query.dto.response.TemporaryPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TripTemporaryStorageQueryController {

    private final TemporarySearchUseCase temporarySearchUseCase;

    @GetMapping("/api/trips/{tripId}/temporary-storage")
    @ResponseStatus(HttpStatus.OK)
    public TemporaryPageResponse findTripTemporaryStorage(@PathVariable Long tripId, @ModelAttribute @Valid TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {
        Slice<ScheduleSummary> scheduleSummaries = temporarySearchUseCase.searchTemporary(tripId,tempSchedulePageCondition,pageable);
        return TemporaryPageResponse.from(scheduleSummaries);
    }
}
