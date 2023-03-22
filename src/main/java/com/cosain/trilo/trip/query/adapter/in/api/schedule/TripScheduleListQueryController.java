package com.cosain.trilo.trip.query.adapter.in.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행의 일정 목록 조회
 */
@Slf4j
@RestController
public class TripScheduleListQueryController {

    @GetMapping("/api/trips/{tripId}/schedules")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String findTripScheduleList(@PathVariable Long tripId) {
        log.info("tripId = {}", tripId);
        return "[여행의 일정 목록 조회] This operation is not implemented.";
    }
}
