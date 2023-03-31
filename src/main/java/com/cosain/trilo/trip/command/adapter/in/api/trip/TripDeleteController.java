package com.cosain.trilo.trip.command.adapter.in.api.trip;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * TODO: 여행 삭제
 */
@Slf4j
@RestController
public class TripDeleteController {

    @DeleteMapping("/api/trips/{tripId}")
    public String deleteTrip(@PathVariable Long tripId) {
        throw new NotImplementedException("여행 삭제 미구현");
    }
}
