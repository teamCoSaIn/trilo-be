package com.cosain.trilo.trip.command.adapter.in.api.trip;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행 수정
 */
@Slf4j
@RestController
public class TripUpdateController {

    @PutMapping("/api/trips/{tripId}")
    public String updateTrip(@PathVariable Long tripId) {
        throw new NotImplementedException("여행 수정 미구현");
    }
}
