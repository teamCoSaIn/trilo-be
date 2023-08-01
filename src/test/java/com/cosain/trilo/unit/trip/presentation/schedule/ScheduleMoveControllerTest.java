package com.cosain.trilo.unit.trip.presentation.schedule;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleMoveController;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleMoveRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 일정 이동을 담당하는 Controller({@link ScheduleMoveController})의 테스트 코드 클래스입니다.
 * @see ScheduleMoveController
 */
@DisplayName("일정 이동 API 테스트")
@WebMvcTest(ScheduleMoveController.class)
public class ScheduleMoveControllerTest extends RestControllerTest {

    /**
     * {@link ScheduleMoveController}의 의존성
     */
    @MockBean
    private ScheduleMoveService scheduleMoveService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final static String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>일정 이동 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>일정 이동 성공 응답이 와야합니다. (200 OK, 본문 있음)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 올바른 요청 -> 일정 이동됨")
    public void moveSchedule_with_authorizedUser() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        var moveResult = ScheduleMoveResult.builder()
                .scheduleId(scheduleId)
                .beforeDayId(1L)
                .afterDayId(targetDayId)
                .positionChanged(true)
                .build();

        var request = new ScheduleMoveRequest(targetDayId, targetOrder);
        var command = ScheduleMoveCommand.of(scheduleId, requestTripperId, targetDayId, targetOrder);

        given(scheduleMoveService.moveSchedule(eq(command)))
                .willReturn(moveResult);

        // when
        ResultActions resultActions = runTest(scheduleId, createJson(request)); // 정상적으로 사용자가 일정 이동 요청했을 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId))
                .andExpect(jsonPath("$.beforeDayId").value(moveResult.getBeforeDayId()))
                .andExpect(jsonPath("$.afterDayId").value(moveResult.getAfterDayId()))
                .andExpect(jsonPath("$.positionChanged").value(moveResult.isPositionChanged())); // 상태코드 및 응답 필드 검증

        verify(scheduleMoveService, times(1)).moveSchedule(eq(command)); // 내부 의존성 호출 검증
    }

    /**
     * <p>Authorization Header에 토큰을 담지 않은 사용자가 요청하면 인증 실패 오류가 발생함을 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (401 UnAuthorized, 토큰 없음)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("토큰 없는 사용자 요청 -> 인증 실패 401")
    public void updateSchedulePlace_withoutToken() throws Exception {
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        var request = new ScheduleMoveRequest(targetDayId, targetOrder);

        // when
        ResultActions resultActions = runTestWithoutAuthority(scheduleId, createJson(request)); // 토큰 없는 사용자의 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 상태 코드 및 에러 응답 검증

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class)); // 서비스 호출 안 함 검증
    }

    /**
     * <p>경로변수로, 숫자가 아닌 일정 식별자 전달 시, 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 Bad Request, 경로 변수 관련 에러)</li>
     *     <li>내부 의존성이 호출되지 않아야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("scheduleId가 숫자가 아닌 값 -> 경로변수 오류 400")
    public void updateSchedule_with_invalidScheduleId() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        String invalidScheduleId = "가가가";
        Long targetDayId = 2L;
        int targetOrder = 3;

        var request = new ScheduleMoveRequest(targetDayId, targetOrder);

        // when
        ResultActions resultActions = runTest(invalidScheduleId, createJson(request)); // 숫자가 아닌 일정 식별자로 일정 이동 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists()); // 응답 메시지 검증

        // 내부 의존성 호출 안 됨
        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
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
    public void moveSchedule_with_emptyContent() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        String emptyContent = "";
        Long scheduleId = 1L;

        // when
        ResultActions resultActions = runTest(scheduleId, emptyContent); // 비어있는 본문으로 요청할 때
        // then

        // 상태 코드 및 응답 에러 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 서비스 호출 안 됨 검증
        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
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
    public void moveSchedule_with_invalidContent() throws Exception {
        // given
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        Long scheduleId = 1L;
        String invalidContent = """
                {
                    "targetDayId": 따옴표로 감싸지 않은 값,
                    "targetOrder": 123
                }
                """;

        // when
        ResultActions resultActions = runTest(scheduleId, invalidContent);  // 형식이 올바르지 않은 body를 담아 요청할 때

        // then

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 서비스가 호출되지 않음을 검증
        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
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
    public void moveSchedule_with_invalidType() throws Exception {
        // given
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        Long scheduleId = 1L;
        String invalidTypeContent = """
                {
                    "targetDayId": 1,
                    "targetOrder": "숫자가 아닌 값"
                }
                """;

        // when

        // 타입이 맞지 않는 필드가 포함된 body
        ResultActions resultActions = runTest(scheduleId, invalidTypeContent);

        // then

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 서비스가 호출되지 않음을 검증
        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param scheduleId : 일정 식별자(id)
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * 토큰이 없는(미인증) 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param scheduleId : 일정 식별자(id)
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithoutAuthority(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

}
