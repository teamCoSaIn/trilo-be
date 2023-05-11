package com.cosain.trilo.trip.query.presentation.trip;

import com.cosain.trilo.trip.query.application.dto.TripPageResult;
import com.cosain.trilo.trip.query.application.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TripperTripListQueryController {

    private final TripListSearchUseCase tripListSearchUseCase;
    @GetMapping("/api/trips")
    @ResponseStatus(HttpStatus.OK)
    public TripPageResponse findTripperTripList(@RequestParam("tripper-id") Long tripperId, Pageable pageable) {
        TripPageResult tripPageResult = tripListSearchUseCase.searchTripDetails(tripperId, pageable);
        return TripPageResponse.from(tripPageResult);
    }
}
