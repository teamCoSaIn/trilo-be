package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.trip.service.trip_title_update.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_title_update.TripTitleUpdateService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripTitleUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripTitleUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripTitleUpdateController {

    private final TripTitleUpdateService tripTitleUpdateService;

    @PutMapping("/api/trips/{tripId}/title")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public TripTitleUpdateResponse updateTrip(@LoginUser UserPayload userPayload, @PathVariable Long tripId, @RequestBody TripTitleUpdateRequest request) {
        Long requestTripperId = userPayload.getId();

        var command = TripTitleUpdateCommand.of(tripId, requestTripperId, request.getTitle());
        tripTitleUpdateService.updateTripTitle(command);

        return new TripTitleUpdateResponse(tripId);
    }
}
