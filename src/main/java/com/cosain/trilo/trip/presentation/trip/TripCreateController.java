package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateService;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 여행 생성 웹 요청을 처리하는 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TripCreateController {

    /**
     * 여행 생성 서비스
     */
    private final TripCreateService tripCreateService;

    /**
     * 사용자 여행 생성 웹 요청({@link TripCreateRequest})을 받아 처리 후, 생성된 여행에 대한 정보({@link TripCreateResponse})를 응답합니다.
     * @param userPayload : 인증 사용자 정보
     * @param request : 사용자 여행 생성 웹 요청
     * @return 생성된 여행에 대한 정보
     * @throws CustomValidationException : 비즈니스 입력 검증에서 발생한 검증 예외
     * @see TripCreateRequest
     * @see TripCreateResponse
     */
    @Login
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/trips")
    public TripCreateResponse createTrip(@LoginUser UserPayload userPayload, @RequestBody TripCreateRequest request) throws CustomValidationException {
        Long tripperId = userPayload.getId();
        var command = TripCreateCommand.of(tripperId, request.getTitle()); // 비즈니스 입력 모델로 변환하는 과정에서 검증 예외 발생할 수 있음

        Long tripId = tripCreateService.createTrip(command);
        return TripCreateResponse.from(tripId);
    }

}
