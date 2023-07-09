package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.service.TripPeriodUpdateService;
import com.cosain.trilo.trip.application.trip.dto.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripPeriodUpdateResponse;
import com.cosain.trilo.user.domain.User;
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
    public TripPeriodUpdateResponse updateTrip(@LoginUser User user, @PathVariable Long tripId, @RequestBody TripPeriodUpdateRequest request) {
        Long tripperId = user.getId();
        TripPeriodUpdateCommand updateCommand = tripPeriodUpdateCommandFactory.createCommand(request.getStartDate(), request.getEndDate());

        tripPeriodUpdateService.updateTripPeriod(tripId, tripperId, updateCommand);
        return new TripPeriodUpdateResponse(tripId);
    }
}
