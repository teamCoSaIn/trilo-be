package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.common.exception.trip.NoTripDeleteAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.trip_delete.TripDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 여행 삭제 웹 요청을 처리하는 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TripDeleteController {

    /**
     * 여행 삭제 서비스
     */
    private final TripDeleteService tripDeleteService;

    /**
     * 사용자의 여행 삭제 요청을 받아 처리하고, 컨텐츠가 없음을 나타내는 상태코드({@link HttpStatus#NO_CONTENT})를 응답합니다.
     * @param userPayload 인증 사용자 정보
     * @param tripId 삭제할 여행 id(식별자)
     * @throws TripNotFoundException 일치하는 식별자의 여행을 찾지 못 했을 때
     * @throws NoTripDeleteAuthorityException 여행 삭제 권한이 없을 때
     */
    @Login
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/trips/{tripId}")
    public void deleteTrip(@LoginUser UserPayload userPayload, @PathVariable Long tripId) throws TripNotFoundException, NoTripDeleteAuthorityException {
        Long tripperId = userPayload.getId();
        tripDeleteService.deleteTrip(tripId, tripperId); // 여행 삭제 -> 여행이 없거나, 삭제권한 없을 때 예외 발생할 수 있음에 주의
    }
}
