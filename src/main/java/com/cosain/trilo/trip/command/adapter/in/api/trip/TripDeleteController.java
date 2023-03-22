package com.cosain.trilo.trip.command.adapter.in.api.trip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * TODO: 여행 삭제
 */
@Slf4j
@RestController
public class TripDeleteController {

    @DeleteMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String deleteTrip(@PathVariable Long tripId) {
        log.info("tripId = {}", tripId);
        return "[여행 삭제] This operation is not implemented.";
    }
}
