package com.cosain.trilo.trip.presentation.schedule;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.common.exception.day.DayNotFoundException;
import com.cosain.trilo.common.exception.day.InvalidTripDayException;
import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.common.exception.schedule.NoScheduleMoveAuthorityException;
import com.cosain.trilo.common.exception.schedule.ScheduleNotFoundException;
import com.cosain.trilo.common.exception.schedule.TooManyDayScheduleException;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveService;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleMoveRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleMoveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 일정 이동 웹 요청을 처리하는 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleMoveController {

    /**
     * 일정 이동 서비스
     */
    private final ScheduleMoveService scheduleMoveService;

    /**
     * 일정 이동 웹 요청({@link ScheduleMoveRequest})을 받아 처리 후,
     * 일정 이동 결과를({@link ScheduleMoveResponse})를 응답합니다.
     * @param userPayload 인증 사용자 정보
     * @param scheduleId 이동할 일정의 식별자(id)
     * @param request 일정 이동 웹 요청
     * @return 일정 이동 응답
     * @throws CustomValidationException 비즈니스 입력검증에서 검증 예외가 발생했을 때
     * @throws ScheduleNotFoundException 일정을 찾을 수 없을 때
     * @throws DayNotFoundException 도착지 Day를 찾을 수 없을 때
     * @throws NoScheduleMoveAuthorityException 일정을 이동할 권한이 없을 때
     * @throws TooManyDayScheduleException 도착지 Day가 가진 일정의 갯수 제한을 넘을 때
     * @throws InvalidTripDayException 도착지 Day가 일정이 속한 Trip의 Day가 아닐 때
     * @throws InvalidScheduleMoveTargetOrderException 요청한 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     */
    @Login
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/schedules/{scheduleId}/position")
    public ScheduleMoveResponse moveSchedule(@LoginUser UserPayload userPayload, @PathVariable Long scheduleId, @RequestBody ScheduleMoveRequest request)
            throws CustomValidationException, ScheduleNotFoundException, DayNotFoundException, NoScheduleMoveAuthorityException, TooManyDayScheduleException,
            InvalidTripDayException, InvalidScheduleMoveTargetOrderException {

        Long requestTripperId = userPayload.getId();

        // 비즈니스 입력 모델 생성 -> 입력 검증과정에서 검증 예외발생할 수 있음
        var command = ScheduleMoveCommand.of(scheduleId, requestTripperId, request.getTargetDayId(), request.getTargetOrder());

        var scheduleMoveResult = scheduleMoveService.moveSchedule(command);
        return ScheduleMoveResponse.from(scheduleMoveResult);
    }
}
