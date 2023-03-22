package com.cosain.trilo.trip.command.adapter.in.api.scheduleplace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 일정장소 수정
 */
@Slf4j
@RestController
public class SchedulePlaceUpdateController {

    @PutMapping("/api/schedule-places/{schedulePlaceId}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String updateSchedulePlace(@PathVariable Long schedulePlaceId) {
        log.info("schedulePlaceId = {}", schedulePlaceId);
        return "[일정장소 수정] This operation is not implemented.";
    }
}
