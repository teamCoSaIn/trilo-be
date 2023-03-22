package com.cosain.trilo.trip.command.adapter.in.api.trip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행 생성
 */
@Slf4j
@RestController
public class TripCreateController {

    @PostMapping("/api/trips")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String createTrip() {
        return "[여행 생성] This operation is not implemented.";
    }

}
