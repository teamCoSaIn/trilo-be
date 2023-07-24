package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveService;
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

    @PutMapping("/api/schedules/{scheduleId}/position")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public ScheduleMoveResponse moveSchedule(@LoginUser UserPayload userPayload, @PathVariable Long scheduleId, @RequestBody ScheduleMoveRequest request) {
        Long requestTripperId = userPayload.getId();

        var command = ScheduleMoveCommand.of(scheduleId, requestTripperId, request.getTargetDayId(), request.getTargetOrder());
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(command);

        return ScheduleMoveResponse.from(scheduleMoveResult);
    }
}
