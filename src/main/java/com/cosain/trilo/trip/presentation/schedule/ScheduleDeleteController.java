package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.common.LoginUser;
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
    public void deleteSchedule(@LoginUser User user, @PathVariable Long scheduleId) {
        Long deleteTripperId = user.getId();
        scheduleDeleteService.deleteSchedule(scheduleId, deleteTripperId);
    }
}
