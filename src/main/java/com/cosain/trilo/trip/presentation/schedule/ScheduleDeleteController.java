package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.infra.jwt.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.schedule.service.ScheduleDeleteService;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleDeleteController {

    private final ScheduleDeleteService scheduleDeleteService;

    @DeleteMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Login
    public void deleteSchedule(@LoginUser UserPayload userPayload, @PathVariable Long scheduleId) {
        Long deleteTripperId = userPayload.getId();
        scheduleDeleteService.deleteSchedule(scheduleId, deleteTripperId);
    }
}
