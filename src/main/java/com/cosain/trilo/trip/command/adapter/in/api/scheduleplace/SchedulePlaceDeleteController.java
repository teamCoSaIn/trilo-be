package com.cosain.trilo.trip.command.adapter.in.api.scheduleplace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정장소 삭제
 */
@Slf4j
@RestController
public class SchedulePlaceDeleteController {

    @DeleteMapping("/api/schedule-places/{schedulePlaceId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String deleteSchedulePlace(@PathVariable Long schedulePlaceId) {
        log.info("schedulePlaceId = {}", schedulePlaceId);
        return "[일정장소 삭제] This operation is not implemented.";
    }
}
