package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.trip.application.trip.service.TripListSearchService;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPageCondition;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    public TripPageResponse findTripperTripList(@ModelAttribute TripPageCondition tripPageCondition, Pageable pageable) {
        Slice<TripSummary> tripSummaries = tripListSearchService.searchTripSummaries(tripPageCondition, pageable);
        return TripPageResponse.from(tripSummaries);
    }
}
