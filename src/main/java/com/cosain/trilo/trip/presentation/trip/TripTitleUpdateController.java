package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.infra.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.trip.service.TripTitleUpdateService;
import com.cosain.trilo.trip.application.trip.dto.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripTitleUpdateCommandFactory;
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
    private final TripTitleUpdateCommandFactory tripTitleUpdateCommandFactory;

    @PutMapping("/api/trips/{tripId}/title")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public TripTitleUpdateResponse updateTrip(@LoginUser UserPayload userPayload, @PathVariable Long tripId, @RequestBody TripTitleUpdateRequest request) {
        Long tripperId = userPayload.getId();

        TripTitleUpdateCommand updateCommand = tripTitleUpdateCommandFactory.createCommand(request.getTitle());
        tripTitleUpdateService.updateTripTitle(tripId, tripperId, updateCommand);

        return new TripTitleUpdateResponse(tripId);
    }
}
