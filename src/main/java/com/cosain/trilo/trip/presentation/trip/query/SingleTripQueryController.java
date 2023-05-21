package com.cosain.trilo.trip.presentation.trip.query;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.query.service.dto.TripResult;
import com.cosain.trilo.trip.application.trip.query.service.TripDetailSearchUseCase;
import com.cosain.trilo.trip.presentation.trip.query.dto.response.TripDetailResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class SingleTripQueryController {

    private final TripDetailSearchUseCase tripDetailSearchUseCase;

    @GetMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.OK)
    public TripDetailResponse findSingleTrip(@LoginUser User user, @PathVariable Long tripId) {

        Long tripperId = user.getId();

        TripResult tripResult = tripDetailSearchUseCase.searchTripDetail(tripId, tripperId);
        return TripDetailResponse.from(tripResult);
    }
}
