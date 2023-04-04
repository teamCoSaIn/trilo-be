package com.cosain.trilo.trip.query.adapter.in.api.scheduleplace;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정장소 단건 조회
 */
@Slf4j
@RestController
public class SingleSchedulePlaceQueryController {

    @GetMapping("/api/schedule-places/{schedulePlaceId}")
    public String findSingleSchedulePlace(@PathVariable Long schedulePlaceId) {
        throw new NotImplementedException("일정장소 단건 조회 미구현");
    }
}
