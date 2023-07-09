package com.cosain.trilo.trip.presentation.schedule.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleCreateService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.dto.factory.ScheduleCreateCommandFactory;
import com.cosain.trilo.trip.presentation.exception.NullRequestCoordinateException;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.RequestCoordinate;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.command.dto.response.ScheduleCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleCreateController {

    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleCreateCommandFactory scheduleCreateCommandFactory;

    @PostMapping("/api/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleCreateResponse createSchedule(@LoginUser User user, @RequestBody ScheduleCreateRequest request) {
        Long tripperId = user.getId();
        ScheduleCreateCommand command = createCommand(request);

        Long scheduleId = scheduleCreateService.createSchedule(tripperId, command);
        log.info("scheduleId = {}", scheduleId);
        return ScheduleCreateResponse.from(scheduleId);
    }

    private ScheduleCreateCommand createCommand(ScheduleCreateRequest request) {
        List<CustomException> exceptions = new ArrayList<>();

        RequestCoordinate requestCoordinate = request.getCoordinate();

        if (requestCoordinate == null) {
            // 사용자가 좌표를 누락시킨 경우, 예외 수집기에 좌표 누락 예외를 수집하고, 요청좌표를 위도 경도가 모두 null 인 요청좌표로 대체함
            exceptions.add(new NullRequestCoordinateException("입력 좌표가 누락됨"));
            requestCoordinate = new RequestCoordinate(null, null);
        }

        return scheduleCreateCommandFactory.createCommand(
                    request.getDayId(), request.getTripId(), request.getTitle(),
                    request.getPlaceId(), request.getPlaceName(),
                    requestCoordinate.getLatitude(), requestCoordinate.getLongitude(), exceptions);
    }

}
