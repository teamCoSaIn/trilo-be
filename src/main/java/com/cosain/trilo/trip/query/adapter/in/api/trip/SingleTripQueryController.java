package com.cosain.trilo.trip.query.adapter.in.api.trip;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행 단건 조회
 */
@Slf4j
@RestController
public class SingleTripQueryController {

    @GetMapping("/api/trips/{tripId}")
    public String findSingleTrip(@PathVariable Long tripId) {
        throw new NotImplementedException("여행 단건 조회 미구현");
    }
}
