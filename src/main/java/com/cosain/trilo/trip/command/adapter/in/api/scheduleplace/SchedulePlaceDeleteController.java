package com.cosain.trilo.trip.command.adapter.in.api.scheduleplace;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정장소 삭제
 */
@Slf4j
@RestController
public class SchedulePlaceDeleteController {

    @DeleteMapping("/api/schedule-places/{schedulePlaceId}")
    public String deleteSchedulePlace(@PathVariable Long schedulePlaceId) {
        throw new NotImplementedException("일정장소 삭제 미구현");
    }
}
