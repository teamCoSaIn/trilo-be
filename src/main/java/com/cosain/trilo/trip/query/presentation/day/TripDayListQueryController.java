package com.cosain.trilo.trip.query.presentation.day;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행의 일정 목록 조회
 */
@Slf4j
@RestController
public class TripDayListQueryController {

    @GetMapping("/api/trips/{tripId}/days")
    public String findTripDayList(@PathVariable Long tripId) {
        throw new NotImplementedException("여행의 Day 목록 조회 미구현");
    }
}
