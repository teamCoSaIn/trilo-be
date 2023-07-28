package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripListSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TripperTripListQueryController {

    private final TripListSearchService tripListSearchService;
    @GetMapping("/api/trippers/{tripperId}/trips")
    @ResponseStatus(HttpStatus.OK)
    public TripListSearchResult findTripperTripList(@ModelAttribute TripListSearchRequest request, @PathVariable Long tripperId) {
        var queryParam = TripListQueryParam.of(tripperId, request.getTripId(), request.getSize());
        return tripListSearchService.searchTripList(queryParam);
    }
}
