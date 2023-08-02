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
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateService;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일정 생성 웹 요청을 처리하는 Controller
 */
@RequiredArgsConstructor
@RestController
public class ScheduleCreateController {

    /**
     * 일정 생성 서비스
     */
    private final ScheduleCreateService scheduleCreateService;

    /**
     * 일정 생성 웹 요청({@link ScheduleCreateRequest})을 받아 처리 후,
     * 일정 생성 결과를 ({@link ScheduleCreateResponse}) 응답합니다.
     * @param userPayload 인증 사용자 정보
     * @param request 일정 생성 웹 요청
     * @return 일정 생성 응답
     * @throws CustomValidationException 비즈니스 입력 검증에서 검증 예외 발생했을 때
     * @throws ScheduleNotFoundException 일정을 찾을 수 없을 때
     * @throws DayNotFoundException 도착지 Day를 찾을 수 없을 때
     * @throws NoScheduleMoveAuthorityException 일정을 이동할 권한이 없을 때
     * @throws TooManyDayScheduleException 도착지 Day가 가진 일정의 갯수 제한을 넘을 때
     * @throws InvalidTripDayException 도착지 Day가 일정이 속한 Trip의 Day가 아닐 때
     * @throws InvalidScheduleMoveTargetOrderException 요청한 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     */
    @PostMapping("/api/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @Login
    public ScheduleCreateResponse createSchedule(@LoginUser UserPayload userPayload, @RequestBody @Valid ScheduleCreateRequest request)
            throws CustomValidationException, ScheduleNotFoundException, DayNotFoundException,
            NoScheduleMoveAuthorityException, TooManyDayScheduleException,
            InvalidTripDayException, InvalidScheduleMoveTargetOrderException {

        Long requestTripperId = userPayload.getId();

        // 비즈니스 입력 모델 생성 -> 입력 검증과정에서 검증 예외발생할 수 있음
        var command = makeCommand(request, requestTripperId);

        Long scheduleId = scheduleCreateService.createSchedule(command);
        return ScheduleCreateResponse.from(scheduleId);
    }

    /**
     * 일정 생성 명령을 생성합니다.
     * @param request 일정 생성 요청
     * @param requestTripperId 일정 생성을 시도하는 사용자(여행자)의 식별자
     * @return 일정 생성 명령
     * @throws CustomValidationException 비즈니스 입력 검증에서 검증 예외 발생했을 때
     */
    private static ScheduleCreateCommand makeCommand(ScheduleCreateRequest request, Long requestTripperId) throws CustomValidationException {
        return ScheduleCreateCommand.of(
                requestTripperId,
                request.getTripId(),
                request.getDayId(),
                request.getTitle(),
                request.getPlaceId(),
                request.getPlaceName(),
                request.getCoordinate().getLatitude(),
                request.getCoordinate().getLongitude()
        );
    }

}
