package com.cosain.trilo.trip.presentation.trip.query;

import com.cosain.trilo.trip.application.trip.query.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import com.cosain.trilo.trip.presentation.trip.query.dto.response.TripPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TripperTripListQueryController {

    private final TripListSearchUseCase tripListSearchUseCase;
    @GetMapping("/api/trips")
    @ResponseStatus(HttpStatus.OK)
    public TripPageResponse findTripperTripList(@ModelAttribute TripPageCondition tripPageCondition, Pageable pageable) {
        Slice<TripSummary> tripSummaries = tripListSearchUseCase.searchTripSummaries(tripPageCondition, pageable);
        return TripPageResponse.from(tripSummaries);
    }
}
