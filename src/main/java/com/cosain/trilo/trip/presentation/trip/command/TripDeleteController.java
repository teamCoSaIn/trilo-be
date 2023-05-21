package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.command.application.usecase.TripDeleteUseCase;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripDeleteController {

    private final TripDeleteUseCase tripDeleteUseCase;

    @DeleteMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@LoginUser User user, @PathVariable Long tripId) {
        Long tripperId = user.getId();
        tripDeleteUseCase.deleteTrip(tripId, tripperId);
    }
}
