package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.command.service.TripTitleUpdateService;
import com.cosain.trilo.trip.application.trip.dto.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripTitleUpdateCommandFactory;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripTitleUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripTitleUpdateResponse;
import com.cosain.trilo.user.domain.User;
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
    public TripTitleUpdateResponse updateTrip(@LoginUser User user, @PathVariable Long tripId, @RequestBody TripTitleUpdateRequest request) {
        Long tripperId = user.getId();

        TripTitleUpdateCommand updateCommand = tripTitleUpdateCommandFactory.createCommand(request.getTitle());
        tripTitleUpdateService.updateTripTitle(tripId, tripperId, updateCommand);

        return new TripTitleUpdateResponse(tripId);
    }
}
