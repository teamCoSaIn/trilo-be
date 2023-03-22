package com.cosain.trilo.trip.command.adapter.in.api.trip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행 수정
 */
@Slf4j
@RestController
public class TripUpdateController {

    @PutMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String updateTrip(@PathVariable Long tripId) {
        log.info("tripId = {}", tripId);
        return "[여행 수정] This operation is not implemented.";
    }
}
