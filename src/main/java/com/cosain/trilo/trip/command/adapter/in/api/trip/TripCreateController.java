package com.cosain.trilo.trip.command.adapter.in.api.trip;

import com.cosain.trilo.common.exception.NotImplementedException;
import com.cosain.trilo.config.security.dto.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행 생성
 */
@Slf4j
@RestController
public class TripCreateController {

    @PostMapping("/api/trips")
    public String createTrip(@AuthenticationPrincipal UserPrincipal principal) {
        throw new NotImplementedException("여행 생성 미구현");
    }

}
