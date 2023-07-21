package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateService;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ScheduleCreateController {

    private final ScheduleCreateService scheduleCreateService;

    @PostMapping("/api/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @Login
    public ScheduleCreateResponse createSchedule(@LoginUser UserPayload userPayload, @RequestBody @Valid ScheduleCreateRequest request) {
        Long requestTripperId = userPayload.getId();
        var command = makeCommand(request, requestTripperId);

        Long scheduleId = scheduleCreateService.createSchedule(command);
        return ScheduleCreateResponse.from(scheduleId);
    }

    private static ScheduleCreateCommand makeCommand(ScheduleCreateRequest request, Long requestTripperId) {
        return ScheduleCreateCommand.of(
                requestTripperId,
                request.getTripId(),
                request.getDayId(),
                request.getTitle(),
                request.getPlaceId(),
                request.getPlaceName(),
                request.getCoordinate().getLatitude(),
                request.getCoordinate().getLongitude()
        );
    }

}
