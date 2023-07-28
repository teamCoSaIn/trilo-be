package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripConditionSearchService;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TripConditionSearchController {

    private final TripConditionSearchService tripConditionSearchService;

    @GetMapping("/api/trips")
    @ResponseStatus(HttpStatus.OK)
    public TripSearchResponse findTripList(@ModelAttribute @Valid TripSearchRequest tripSearchRequest){
        return tripConditionSearchService.findBySearchConditions(tripSearchRequest);
    }
}
