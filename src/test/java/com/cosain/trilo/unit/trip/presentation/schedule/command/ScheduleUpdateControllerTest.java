package com.cosain.trilo.unit.trip.presentation.schedule.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleUpdateService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.dto.factory.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleUpdateController;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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

    @MockBean
    private ScheduleUpdateCommandFactory scheduleUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 올바른 요청 -> 일정 수정됨")
    public void updateSchedule_with_authorizedUser() throws Exception {

        // given
        mockingForLoginUserAnnotation();

        Long scheduleId = 1L;
        String rawTitle = "수정할 제목";
        String rawContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest(rawTitle, rawContent, startTime, endTime);
        ScheduleUpdateCommand command = new ScheduleUpdateCommand(ScheduleTitle.of(rawContent), ScheduleContent.of(rawContent), ScheduleTime.of(startTime, endTime));

        given(scheduleUpdateCommandFactory.createCommand(eq(rawTitle),eq(rawContent), eq(startTime), eq(endTime))).willReturn(command);
        given(scheduleUpdateService.updateSchedule(eq(scheduleId),any(),any(ScheduleUpdateCommand.class))).willReturn(1L);

        // when & then
        mockMvc.perform(put("/api/schedules/" + scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1L))
                .andDo(print());

        verify(scheduleUpdateCommandFactory).createCommand(eq(rawTitle), eq(rawContent), eq(startTime), eq(endTime));
        verify(scheduleUpdateService).updateSchedule(eq(scheduleId),any(),any(ScheduleUpdateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateSchedule_with_unauthorizedUser() throws Exception {
        Long scheduleId = 1L;
        String rawTitle = "수정할 제목";
        String rawContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest(rawTitle, rawContent, startTime, endTime);

        mockMvc.perform(put("/api/schedules/"+scheduleId)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateSchedule_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        String emptyContent = "";

        mockMvc.perform(put("/api/schedules/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(emptyContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateSchedule_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidContent = """
                {
                    "title": 따옴표로 감싸지 않은 제목,
                    "content": "본문",
                    "startTime": "13:05",
                    "endTime": "13:07"
                }
                """;

        mockMvc.perform(put("/api/schedules/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
