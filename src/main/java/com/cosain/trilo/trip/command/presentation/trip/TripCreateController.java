package com.cosain.trilo.trip.command.presentation.trip;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.command.application.command.TripCreateCommand;
import com.cosain.trilo.trip.command.application.usecase.TripCreateUseCase;
import com.cosain.trilo.trip.command.presentation.trip.dto.TripCreateRequest;
import com.cosain.trilo.trip.command.presentation.trip.dto.TripCreateResponse;
import com.cosain.trilo.user.domain.User;
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

    private final TripCreateUseCase tripCreateUseCase;

    @PostMapping("/api/trips")
    public ResponseEntity<TripCreateResponse> createTrip(@LoginUser User user, @RequestBody TripCreateRequest request) {
        Long tripperId = user.getId();
        TripCreateCommand createCommand = request.toCommand();

        Long tripId = tripCreateUseCase.createTrip(tripperId, createCommand);

        var response = TripCreateResponse.from(tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
