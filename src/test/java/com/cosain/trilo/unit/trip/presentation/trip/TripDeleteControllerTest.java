package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_delete.TripDeleteService;
import com.cosain.trilo.trip.presentation.trip.TripDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 삭제를 담당하는 Controller({@link TripDeleteController})의 테스트 코드 클래스입니다.
 * @see TripDeleteController
 */
@DisplayName("여행 삭제 API 테스트")
@WebMvcTest(TripDeleteController.class)
class TripDeleteControllerTest extends RestControllerTest {

    /**
     * {@link TripDeleteController}의 의존성
     */
    @MockBean
    private TripDeleteService tripDeleteService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final static String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>여행 삭제 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>컨텐츠가 없다는 응답이 와야합니다. 이때 본문은 비어있습니다. (204 No Content)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 올바른 여행 삭제 요청 -> 성공")
    public void deleteTrip_with_authorizedUser() throws Exception {
        // given
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);

        Long tripId = 1L;

        // when
        ResultActions resultActions = runTest(tripId); // 인증 사용자의 여행 삭제 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist()); // 응답 메시지 검증

        verify(tripDeleteService, times(1)).deleteTrip(eq(tripId), eq(requestTripperId)); // 내부 의존성 호출 검증
    }

    /**
     * <p>Authorization Header에 토큰을 담지 않은 사용자가 요청하면 인증 실패 오류가 발생함을 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (401 UnAuthorized, 토큰 없음)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void deleteTrip_with_unauthorizedUser() throws Exception {
        // given
        Long tripId = 1L;

        // when
        ResultActions resultActions = runTestWithoutAuthorization(tripId); // 미인증 사용자의 여행 삭제 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검증

        verify(tripDeleteService, times(0)).deleteTrip(eq(tripId), any(Long.class)); // 내부 의존성 호출 안 됨 검증
    }

    /**
     * <p>경로변수로, 숫자가 아닌 여행 식별자 전달 시, 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 Bad Request, 경로 변수 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void deleteTrip_with_stringTripId() throws Exception {
        // given
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);

        String invalidTripId = "가가가";

        // when
        ResultActions resultActions = runTest(invalidTripId);  // 숫자가 아닌 여행 식별자로 삭제 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검증

        verify(tripDeleteService, times(0)).deleteTrip(anyLong(), anyLong()); // 내부 의존성 호출 안 됨 검증
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId) throws Exception {
        return mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * 미인증 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithoutAuthorization(Object tripId) throws Exception {
        return mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                // 인증 헤더 없음
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
