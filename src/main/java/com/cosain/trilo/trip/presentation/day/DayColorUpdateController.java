package com.cosain.trilo.trip.presentation.day;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.day.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.application.day.service.DayColorUpdateService;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateResponse;
import com.cosain.trilo.user.domain.User;
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
    public DayColorUpdateResponse updateDayColor(
            @PathVariable("dayId") Long dayId,
            @LoginUser User user,
            @RequestBody DayColorUpdateRequest request) {

        Long tripperId = user.getId();

        DayColorUpdateCommand command = dayColorUpdateCommandFactory.createCommand(request.getColorName());

        dayColorUpdateService.updateDayColor(dayId, tripperId, command);
        return new DayColorUpdateResponse(dayId);
    }
}
