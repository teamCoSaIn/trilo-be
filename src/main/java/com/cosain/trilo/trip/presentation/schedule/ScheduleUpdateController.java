package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.ScheduleUpdateService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.dto.factory.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleUpdateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScheduleUpdateController {

    private final ScheduleUpdateService scheduleUpdateService;
    private final ScheduleUpdateCommandFactory scheduleUpdateCommandFactory;

    @PutMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public ScheduleUpdateResponse updateSchedule(@LoginUser UserPayload userPayload, @PathVariable Long scheduleId, @RequestBody ScheduleUpdateRequest request) {
        Long tripperId = userPayload.getId();

        ScheduleUpdateCommand scheduleUpdateCommand = scheduleUpdateCommandFactory.createCommand(request.getTitle(),request.getContent(), request.getStartTime(), request.getEndTime());
        Long updatedScheduleId = scheduleUpdateService.updateSchedule(scheduleId,tripperId, scheduleUpdateCommand);

        return ScheduleUpdateResponse.from(updatedScheduleId);
    }

}
