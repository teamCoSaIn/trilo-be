package com.cosain.trilo.trip.query.adapter.in.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정 단건 조회
 */
@Slf4j
@RestController
public class SingleScheduleQueryController {

    @GetMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String findSingleSchedule(@PathVariable Long scheduleId) {
        log.info("SchedulePlaceId = {}", scheduleId);
        return "[일정 단건 조회] This operation is not implemented.";
    }
}
