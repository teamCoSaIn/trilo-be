package com.cosain.trilo.trip.command.presentation.schedule;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import com.cosain.trilo.trip.command.application.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.command.presentation.schedule.dto.ScheduleCreateRequest;
import com.cosain.trilo.trip.command.presentation.schedule.dto.ScheduleCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/api/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleCreateResponse createSchedule(@LoginUser User user, @RequestBody ScheduleCreateRequest request){
        Long tripperId = user.getId();
        ScheduleCreateCommand createCommand = request.toCommand();

        Long scheduleId = scheduleCreateUseCase.createSchedule(tripperId, createCommand);

        return ScheduleCreateResponse.from(scheduleId);
    }
}
