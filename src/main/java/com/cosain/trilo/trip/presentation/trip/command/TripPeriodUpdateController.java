package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.command.usecase.TripPeriodUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripPeriodUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.command.dto.response.TripPeriodUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripPeriodUpdateController {

    private final TripPeriodUpdateUseCase tripPeriodUpdateUseCase;
    private final TripPeriodUpdateCommandFactory tripPeriodUpdateCommandFactory;

    @PutMapping("/api/trips/{tripId}/period")
    @ResponseStatus(HttpStatus.OK)
    public TripPeriodUpdateResponse updateTrip(@LoginUser User user, @PathVariable Long tripId, @RequestBody TripPeriodUpdateRequest request) {
        return null;
    }
}
