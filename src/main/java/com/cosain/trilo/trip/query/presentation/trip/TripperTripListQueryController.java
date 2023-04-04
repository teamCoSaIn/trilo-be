package com.cosain.trilo.trip.query.presentation.trip;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 특정 사용자 여행 목록
 */
@Slf4j
@RestController
public class TripperTripListQueryController {

    @GetMapping("/api/trips")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String findTripperTripList(@RequestParam("tripper-id") Long tripperId) {
        throw new NotImplementedException("특정 사용자 여행 목록 미구현");
    }
}
