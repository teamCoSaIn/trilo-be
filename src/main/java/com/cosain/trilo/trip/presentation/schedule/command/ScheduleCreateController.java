package com.cosain.trilo.trip.presentation.schedule.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleCreateCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.command.dto.response.ScheduleCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleCreateController {

    private final ScheduleCreateUseCase scheduleCreateUseCase;
    private final ScheduleCreateCommandFactory scheduleCreateCommandFactory;

    @PostMapping("/api/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleCreateResponse createSchedule(@LoginUser User user, @RequestBody ScheduleCreateRequest request){
        Long tripperId = user.getId();
        ScheduleCreateCommand command = createCommand(request);

        Long scheduleId = scheduleCreateUseCase.createSchedule(tripperId, command);
        return ScheduleCreateResponse.from(scheduleId);
    }

    private ScheduleCreateCommand createCommand(ScheduleCreateRequest request) {
        return scheduleCreateCommandFactory.createCommand(
                request.getDayId(), request.getTripId(), request.getTitle(),
                request.getPlaceId(), request.getPlaceName(),
                request.getLatitude(), request.getLongitude()
        );
    }
}
