package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListSearchResult;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TemporarySearchService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempScheduleListRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TripTemporaryStorageQueryController {

    private final TemporarySearchService temporarySearchService;

    @GetMapping("/api/trips/{tripId}/temporary-storage")
    @ResponseStatus(HttpStatus.OK)
    public TempScheduleListSearchResult findTripTemporaryStorage(@PathVariable Long tripId, @ModelAttribute TempScheduleListRequest request) {
        var queryParam = TempScheduleListQueryParam.of(tripId, request.getScheduleId(), request.getSize());
        return temporarySearchService.searchTemporary(queryParam);
    }
}
