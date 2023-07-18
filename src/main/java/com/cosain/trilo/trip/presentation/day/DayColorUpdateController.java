package com.cosain.trilo.trip.presentation.day;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.day.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.application.day.service.DayColorUpdateService;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DayColorUpdateController {

    private final DayColorUpdateService dayColorUpdateService;
    private final DayColorUpdateCommandFactory dayColorUpdateCommandFactory;

    @PutMapping("/api/days/{dayId}/color")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public DayColorUpdateResponse updateDayColor(
            @PathVariable("dayId") Long dayId,
            @LoginUser UserPayload userPayload,
            @RequestBody DayColorUpdateRequest request) {

        Long tripperId = userPayload.getId();

        DayColorUpdateCommand command = dayColorUpdateCommandFactory.createCommand(request.getColorName());

        dayColorUpdateService.updateDayColor(dayId, tripperId, command);
        return new DayColorUpdateResponse(dayId);
    }
}
