package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.temporary_search.TemporarySearchService;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import com.cosain.trilo.trip.presentation.trip.dto.response.TemporaryPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TripTemporaryStorageQueryController {

    private final TemporarySearchService temporarySearchService;

    @GetMapping("/api/trips/{tripId}/temporary-storage")
    @ResponseStatus(HttpStatus.OK)
    public TemporaryPageResponse findTripTemporaryStorage(@PathVariable Long tripId, @ModelAttribute @Valid TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {
        Slice<ScheduleSummary> scheduleSummaries = temporarySearchService.searchTemporary(tripId,tempSchedulePageCondition,pageable);
        return TemporaryPageResponse.from(scheduleSummaries);
    }
}
