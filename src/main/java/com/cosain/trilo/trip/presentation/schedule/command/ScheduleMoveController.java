package com.cosain.trilo.trip.presentation.schedule.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleMoveUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleMoveCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleMoveRequest;
import com.cosain.trilo.trip.presentation.schedule.command.dto.response.ScheduleMoveResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleMoveController {

    private final ScheduleMoveUseCase scheduleMoveUseCase;
    private final ScheduleMoveCommandFactory scheduleMoveCommandFactory;

    @PutMapping("/api/schedules/{scheduleId}/position")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleMoveResponse moveSchedule(@LoginUser User user, @PathVariable Long scheduleId, @RequestBody ScheduleMoveRequest request) {
        Long moveTripperId = user.getId();

        ScheduleMoveCommand scheduleMoveCommand = scheduleMoveCommandFactory.createCommand(request.getTargetDayId(), request.getTargetOrder());
        ScheduleMoveResult scheduleMoveResult = scheduleMoveUseCase.moveSchedule(scheduleId, moveTripperId, scheduleMoveCommand);

        return ScheduleMoveResponse.from(scheduleMoveResult);
    }
}
