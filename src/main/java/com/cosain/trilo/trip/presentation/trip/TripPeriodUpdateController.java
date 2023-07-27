package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.domain.exception.EmptyPeriodUpdateException;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripPeriodUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 여행 기간수정 웹 요청을 처리하는 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TripPeriodUpdateController {

    /**
     * 여행 기간수정 서비스
     */
    private final TripPeriodUpdateService tripPeriodUpdateService;

    /**
     * 여행 기간수정 웹 요청({@link TripPeriodUpdateRequest})을 받아 처리 후,
     * 수정된 여행에 대한 정보({@link TripPeriodUpdateResponse})를 응답합니다.
     * @param userPayload 인증 사용자 정보
     * @param tripId 수정할 여행 Id(식별자)
     * @param request 여행기간 수정 웹 요청
     * @return 여행 기간수정 응답
     * @throws CustomValidationException 비즈니스 입력검증에서 검증 예외가 발생했을 때
     * @throws TripNotFoundException 일치하는 식별자(id)의 여행을 찾지 못 했을 때
     * @throws NoTripUpdateAuthorityException 여행을 수정할 권한이 없을 때
     * @throws EmptyPeriodUpdateException 여행의 기간이 정해진 상태에서, 빈 기간으로 변경하려고 할 때
     */
    @Login
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/trips/{tripId}/period")
    public TripPeriodUpdateResponse updateTrip(
            @LoginUser UserPayload userPayload, @PathVariable Long tripId, @RequestBody TripPeriodUpdateRequest request)
            throws CustomValidationException, TripNotFoundException, NoTripUpdateAuthorityException, EmptyPeriodUpdateException {

        Long requestTripperId = userPayload.getId();

        // 비즈니스 입력 모델 생성 -> 입력 검증과정에서 검증 예외발생할 수 있음
        var command = TripPeriodUpdateCommand.of(tripId, requestTripperId, request.getStartDate(), request.getEndDate());

        tripPeriodUpdateService.updateTripPeriod(command);
        return new TripPeriodUpdateResponse(tripId);
    }
}
