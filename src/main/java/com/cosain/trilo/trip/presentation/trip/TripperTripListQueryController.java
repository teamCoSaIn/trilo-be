package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripListSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TripperTripListQueryController {

    private final TripListSearchService tripListSearchService;
    @GetMapping("/api/trips")
    @ResponseStatus(HttpStatus.OK)
    public TripListSearchResult findTripperTripList(@ModelAttribute TripListSearchRequest request) {
        var queryParam = TripListQueryParam.of(request.getTripperId(), request.getTripId(), request.getSize());
        return tripListSearchService.searchTripList(queryParam);
    }
}
