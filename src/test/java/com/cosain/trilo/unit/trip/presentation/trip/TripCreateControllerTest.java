package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateService;
import com.cosain.trilo.trip.presentation.trip.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 생성을 담당하는 Controller({@link TripCreateController})의 테스트 코드 클래스입니다.
 * @see TripCreateController
 */
@DisplayName("여행 생성 API 테스트")
@WebMvcTest(TripCreateController.class)
class TripCreateControllerTest extends RestControllerTest {

    /**
     * TripCreateController의 의존성
     */
    @MockBean
    private TripCreateService tripCreateService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final static String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>여행 생성 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>생성이 성공됐다는 응답이 와야합니다. (201 Created, 생성된 사용자 식별자)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 여행 생성 요청 -> 성공")
    public void successTest() throws Exception {
        // given
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId); // 인증된 사용자 mocking 됨

        String rawTitle = "제목";
        var request = new TripCreateRequest(rawTitle);

        Long createdTripId = 1L;
        var command = TripCreateCommand.of(requestTripperId, rawTitle);
        given(tripCreateService.createTrip(eq(command))).willReturn(createdTripId); // 여행 생성 후 서비스에서 반환받을 여행 식별자 mocking

        // when
        ResultActions resultActions = runTest(createJson(request)); // 정상적으로 인증된 사용자가 요청했을 때

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(createdTripId)); // 상태코드 및 응답 필드 검증

        verify(tripCreateService, times(1)).createTrip(eq(command)); // 내부 의존성 호출 검증
    }

    /**
     * <p>Authorization Header에 토큰을 담지 않은 사용자가 요청하면 인증 실패 오류가 발생함을 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (401 UnAuthorized, 토큰 없음)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("토큰이 없는 사용자 요청 -> 인증 실패 401")
    public void createTrip_without_token() throws Exception {
        // given
        String rawTitle = "제목";
        TripCreateRequest request = new TripCreateRequest(rawTitle);

        // when
        ResultActions resultActions = runTestWithoutAuthorization(createJson(request)); // 인증하지 않은 사용자가 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 상태 코드 및 에러 응답 검증

        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class)); // 서비스 호출 안 됨 검증
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
    public void createTrip_with_emptyContent() throws Exception {
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String emptyContent = "";

        // when
        ResultActions resultActions = runTest(emptyContent); // 비어있는 본문으로 요청할 때

        // given
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 상태 코드 및 응답 에러 메시지 검증

        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class)); // 서비스 호출 안 됨 검증
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
    public void createTrip_with_invalidContent() throws Exception {
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);
        String invalidContent = """
                {
                    "title": 따옴표 안 감싼 제목
                }
                """;

        // when
        ResultActions resultActions = runTest(invalidContent); // 형식이 올바르지 않은 body를 담아 요청할 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class)); // 서비스가 호출되지 않음을 검증
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(String content) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * 미인증 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithoutAuthorization(String content) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
