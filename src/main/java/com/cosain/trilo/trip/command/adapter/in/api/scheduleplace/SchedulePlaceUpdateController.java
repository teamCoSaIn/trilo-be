package com.cosain.trilo.trip.command.adapter.in.api.scheduleplace;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일정장소 수정
 */
@Slf4j
@RestController
public class SchedulePlaceUpdateController {

    @PutMapping("/api/schedule-places/{schedulePlaceId}")
    public String updateSchedulePlace(@PathVariable Long schedulePlaceId) {
        throw new NotImplementedException("일정장소 수정 미구현");
    }
}
