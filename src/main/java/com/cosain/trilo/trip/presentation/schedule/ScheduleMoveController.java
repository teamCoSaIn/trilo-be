package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.infra.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.ScheduleMoveService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.dto.factory.ScheduleMoveCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleMoveRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleMoveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleMoveController {

    private final ScheduleMoveService scheduleMoveService;
    private final ScheduleMoveCommandFactory scheduleMoveCommandFactory;

    @PutMapping("/api/schedules/{scheduleId}/position")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public ScheduleMoveResponse moveSchedule(@LoginUser UserPayload userPayload, @PathVariable Long scheduleId, @RequestBody ScheduleMoveRequest request) {
        Long moveTripperId = userPayload.getId();

        ScheduleMoveCommand scheduleMoveCommand = scheduleMoveCommandFactory.createCommand(request.getTargetDayId(), request.getTargetOrder());
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, moveTripperId, scheduleMoveCommand);

        return ScheduleMoveResponse.from(scheduleMoveResult);
    }
}
