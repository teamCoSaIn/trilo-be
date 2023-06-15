package com.cosain.trilo.trip.presentation.day.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.day.command.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.application.day.command.usecase.DayColorUpdateUseCase;
import com.cosain.trilo.trip.presentation.day.command.dto.DayColorUpdateRequest;
import com.cosain.trilo.trip.presentation.day.command.dto.DayColorUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DayColorUpdateController {

    private final DayColorUpdateUseCase dayColorUpdateUseCase;
    private final DayColorUpdateCommandFactory dayColorUpdateCommandFactory;

    @PutMapping("/api/days/{dayId}/color")
    public DayColorUpdateResponse updateDayColor(
            @PathVariable("dayId") Long dayId,
            @LoginUser User user,
            @RequestBody DayColorUpdateRequest request) {
        return null;
    }
}
