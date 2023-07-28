package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.presentation.trip.TripPeriodUpdateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 기간수정을 담당하는 Controller({@link TripPeriodUpdateController})의 테스트 코드 클래스입니다.
 * @see TripPeriodUpdateController
 */
@DisplayName("여행 기간 수정 API 테스트")
@WebMvcTest(TripPeriodUpdateController.class)
class TripPeriodUpdateControllerTest extends RestControllerTest {

    /**
     * {@link TripPeriodUpdateController}의 의존성
     */
    @MockBean
    private TripPeriodUpdateService tripPeriodUpdateService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>여행 기간 수정 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>여행 기간 변경 성공 응답이 와야합니다. (200 OK, 본문 있음)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripPeriod_with_authorizedUser() throws Exception {
        // given
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId); // 인증된 사용자 mocking

        Long tripId = 1L;

        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);
        var request = new TripPeriodUpdateRequest(startDate, endDate);

        var command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);

        // when
        ResultActions resultActions = runTest(tripId, createJson(request)); // 정상적으로 사용자가 여행 기간 수정 요청했을 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId)); // 상태코드 및 응답 필드 검증

        verify(tripPeriodUpdateService, times(1)).updateTripPeriod(eq(command)); // 내부 의존성 호출 검증
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
    public void updateTripPeriod_with_unauthorizedUser() throws Exception {
        // given
        Long tripId = 1L;

        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        var request = new TripPeriodUpdateRequest(startDate, endDate);

        // when
        ResultActions resultActions = runTestWithoutAuthorization(tripId, createJson(request)); // 토큰 없는 사용자의 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 상태 코드 및 에러 응답 검증


        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class)); // 서비스 호출 안 됨 검증
    }

    /**
     * <p>경로변수로, 숫자가 아닌 여행 식별자 전달 시, 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 Bad Request, 경로 변수 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("tripId로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateTripPeriod_with_notNumberTripId() throws Exception {
        // given
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String notNumberTripId = "가가가";
        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        var request = new TripPeriodUpdateRequest(startDate, endDate);

        // when
        ResultActions resultActions = runTest(notNumberTripId, createJson(request)); // 숫자가 아닌 여행 식별자로 여행 이동 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검증

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class)); // 내부 의존성 호출 안 됨 검증
    }

    /**
     * <p>비어있는 본문으로 요청 시, 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 Bad Request, 형식이 올바르지 않은 바디 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_emptyContent() throws Exception {
        // given
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 2L;
        String emptyContent = "";

        // when
        ResultActions resultActions = runTest(tripId, emptyContent); // 비어있는 본문으로 요청할 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 상태 코드 및 응답 에러 메시지 검증

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class)); // 서비스 호출 안 됨 검증
    }

    /**
     * 형식이 올바르지 않은 본문을 바디에 담아 요청할 때 에러가 발생함을 검증합니다.
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 BadRequest, 형식이 올바르지 않은 바디 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidContent() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        String invalidContent = """
                {
                    "startDate" + 2023-03-01,
                    "endDate": "2023-03-02"
                }
                """;

        // when
        ResultActions resultActions = runTest(tripId, invalidContent); // 형식이 올바르지 않은 body를 담아 요청할 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class)); // 서비스가 호출되지 않음을 검증
    }

    /**
     * 타입이 올바르지 않은 필드가 포함된 본문을 보낼 때 에러가 발생함을 검증합니다.
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 BadRequest, 형식이 올바르지 않은 데이터 형식, 데이터 타입 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidType() throws Exception {
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        String invalidTypeContent = """
                {
                    "title": "제목",
                    "startDate": "2023-03-01",
                    "endDate": "날짜형식이 아닌 문자열"
                }
                """;

        // when
        ResultActions resultActions = runTest(tripId, invalidTypeContent); // 타입이 맞지 않는 필드가 포함된 body

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검증

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class)); // 서비스가 호출되지 않음을 검증
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId, String content) throws Exception {
        return mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * 토큰이 없는(미인증) 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithoutAuthorization(Object tripId, String content) throws Exception {
        return mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
