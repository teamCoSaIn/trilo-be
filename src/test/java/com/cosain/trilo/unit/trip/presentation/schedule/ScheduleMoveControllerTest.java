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

@DisplayName("일정 이동 API 테스트")
@WebMvcTest(ScheduleMoveController.class)
public class ScheduleMoveControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleMoveService scheduleMoveService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 올바른 요청 -> 일정 이동됨")
    public void moveSchedule_with_authorizedUser() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        ScheduleMoveResult moveResult = ScheduleMoveResult.builder()
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
        ResultActions resultActions = runTest(scheduleId, createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId))
                .andExpect(jsonPath("$.beforeDayId").value(moveResult.getBeforeDayId()))
                .andExpect(jsonPath("$.afterDayId").value(moveResult.getAfterDayId()))
                .andExpect(jsonPath("$.positionChanged").value(moveResult.isPositionChanged()));

        verify(scheduleMoveService, times(1)).moveSchedule(eq(command));
    }

    @Test
    @DisplayName("토큰 없는 사용자 요청 -> 인증 실패 401")
    public void updateSchedulePlace_withoutToken() throws Exception {
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        var request = new ScheduleMoveRequest(targetDayId, targetOrder);

        // when
        ResultActions resultActions = runTestWithoutAuthority(scheduleId, createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }

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
        ResultActions resultActions = runTest(invalidScheduleId, createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void moveSchedule_with_emptyContent() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        String emptyContent = "";
        Long scheduleId = 1L;

        // when
        ResultActions resultActions = runTest(scheduleId, emptyContent);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }

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
        ResultActions resultActions = runTest(scheduleId, invalidContent);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }

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
        ResultActions resultActions = runTest(scheduleId, invalidTypeContent);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleMoveService, times(0)).moveSchedule(any(ScheduleMoveCommand.class));
    }

    private ResultActions runTest(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions runTestWithoutAuthority(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

}
