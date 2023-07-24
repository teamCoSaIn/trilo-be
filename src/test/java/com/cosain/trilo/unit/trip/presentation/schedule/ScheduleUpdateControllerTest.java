package com.cosain.trilo.unit.trip.presentation.schedule;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleUpdateController;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("일정 수정 API 테스트")
@WebMvcTest(ScheduleUpdateController.class)
class ScheduleUpdateControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleUpdateService scheduleUpdateService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 올바른 요청 -> 일정 수정됨")
    public void updateSchedule_with_authorizedUser() throws Exception {
        // given
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        Long scheduleId = 1L;
        String rawScheduleTitle = "수정할 제목";
        String rawScheduleContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        var request = new ScheduleUpdateRequest(rawScheduleTitle, rawScheduleContent, startTime, endTime);
        var command = ScheduleUpdateCommand.of(scheduleId, requestTripperId, rawScheduleTitle, rawScheduleContent, startTime, endTime);

        // when
        ResultActions resultActions = runTest(scheduleId, createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId));

        verify(scheduleUpdateService, times(1)).updateSchedule(eq(command));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void updateSchedule_with_unauthorizedUser() throws Exception {
        // given
        Long scheduleId = 1L;
        String rawScheduleTitle = "수정할 제목";
        String rawScheduleContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest(rawScheduleTitle, rawScheduleContent, startTime, endTime);

        // when
        ResultActions resultActions = runTestWithoutAuthority(scheduleId, createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleUpdateService, times(0)).updateSchedule(any(ScheduleUpdateCommand.class));
    }

    @Test
    @DisplayName("scheduleId 가 숫자가 아님 -> 경로변수 오류 400 응답")
    public void updateSchedule_with_invalidScheduleId() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        String invalidScheduleId = "가가가";
        String rawScheduleTitle = "수정할 제목";
        String rawScheduleContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest(rawScheduleTitle, rawScheduleContent, startTime, endTime);

        // given
        ResultActions resultActions = runTest(invalidScheduleId, createJson(request));

        // when
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleUpdateService, times(0)).updateSchedule(any(ScheduleUpdateCommand.class));
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateSchedule_with_emptyContent() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        long scheduleId = 1L;
        String emptyContent = "";

        // when
        ResultActions resultActions = runTest(scheduleId, emptyContent);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleUpdateService, times(0)).updateSchedule(any(ScheduleUpdateCommand.class));
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateSchedule_with_invalidContent() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);
        long scheduleId = 2L;
        String invalidContent = """
                {
                    "title": 따옴표로 감싸지 않은 제목,
                    "content": "본문",
                    "startTime": "13:05",
                    "endTime": "13:07"
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

        verify(scheduleUpdateService, times(0)).updateSchedule(any(ScheduleUpdateCommand.class));
    }

    private ResultActions runTest(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions runTestWithoutAuthority(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
