package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.command.usecase.TripImageUpdateUseCase;
import com.cosain.trilo.trip.presentation.trip.command.dto.response.TripImageUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripImageUpdateController {

    private final TripImageUpdateUseCase tripImageUpdateUseCase;

    @PutMapping("/api/trips/{tripId}/image")
    @ResponseStatus(HttpStatus.OK)
    public TripImageUpdateResponse updateTripImage(
            @LoginUser User user,
            @PathVariable Long tripId,
            @RequestParam("image") MultipartFile file) {

        return null;
    }

}
