package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.ScheduleUpdateService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.dto.factory.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleUpdateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleUpdateResponse;
import com.cosain.trilo.user.domain.User;
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
    public ScheduleUpdateResponse updateSchedule(@LoginUser User user, @PathVariable Long scheduleId, @RequestBody ScheduleUpdateRequest request) {
        Long tripperId = user.getId();

        ScheduleUpdateCommand scheduleUpdateCommand = scheduleUpdateCommandFactory.createCommand(request.getTitle(),request.getContent(), request.getStartTime(), request.getEndTime());
        Long updatedScheduleId = scheduleUpdateService.updateSchedule(scheduleId,tripperId, scheduleUpdateCommand);

        return ScheduleUpdateResponse.from(updatedScheduleId);
    }

}
