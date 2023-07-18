package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripPeriodUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripPeriodUpdateController {

    private final TripPeriodUpdateService tripPeriodUpdateService;
    private final TripPeriodUpdateCommandFactory tripPeriodUpdateCommandFactory;

    @PutMapping("/api/trips/{tripId}/period")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public TripPeriodUpdateResponse updateTrip(@LoginUser UserPayload userPayload, @PathVariable Long tripId, @RequestBody TripPeriodUpdateRequest request) {
        Long tripperId = userPayload.getId();
        TripPeriodUpdateCommand updateCommand = tripPeriodUpdateCommandFactory.createCommand(request.getStartDate(), request.getEndDate());

        tripPeriodUpdateService.updateTripPeriod(tripId, tripperId, updateCommand);
        return new TripPeriodUpdateResponse(tripId);
    }
}
