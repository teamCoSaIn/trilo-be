package com.cosain.trilo.trip.query.adapter.in.api.scheduleplace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정장소 단건 조회
 */
@Slf4j
@RestController
public class SingleSchedulePlaceQueryController {

    @GetMapping("/api/schedule-places/{schedulePlaceId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String findSingleSchedulePlace(@PathVariable Long schedulePlaceId) {
        log.info("SchedulePlaceId = {}", schedulePlaceId);
        return "[일정장소 단건 조회] This operation is not implemented.";
    }
}
