package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.infra.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.trip.service.TripCreateService;
import com.cosain.trilo.trip.application.trip.dto.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripCreateCommandFactory;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripCreateController {

    private final TripCreateService tripCreateService;
    private final TripCreateCommandFactory tripCreateCommandFactory;

    @PostMapping("/api/trips")
    @Login
    public ResponseEntity<TripCreateResponse> createTrip(@LoginUser UserPayload userPayload, @RequestBody TripCreateRequest request) {
        Long tripperId = userPayload.getId();
        TripCreateCommand createCommand = tripCreateCommandFactory.createCommand(request.getTitle());

        Long tripId = tripCreateService.createTrip(tripperId, createCommand);

        var response = TripCreateResponse.from(tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
