package com.cosain.trilo.unit.trip.presentation.schedule.command;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleMoveUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleMoveCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleMoveController;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleMoveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("일정 이동 API 테스트")
@WebMvcTest(ScheduleMoveController.class)
public class ScheduleMoveControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleMoveUseCase scheduleMoveUseCase;

    @MockBean
    private ScheduleMoveCommandFactory scheduleMoveCommandFactory;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    private static final String ENDPOINT_URL_TEMPLATE = "/api/schedules/{scheduleId}/position";

    @Test
    @DisplayName("인증된 사용자의 올바른 요청 -> 일정 이동됨")
    @WithMockUser
    public void moveSchedule_with_authorizedUser() throws Exception {
        mockingForLoginUserAnnotation();
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        ScheduleMoveResult moveResult = ScheduleMoveResult.builder()
                .scheduleId(scheduleId)
                .beforeDayId(1L)
                .afterDayId(targetDayId)
                .positionChanged(true)
                .build();

        ScheduleMoveRequest request = new ScheduleMoveRequest(targetDayId, targetOrder);
        ScheduleMoveCommand command = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleMoveCommandFactory.createCommand(eq(targetDayId), eq(targetOrder)))
                .willReturn(command);
        given(scheduleMoveUseCase.moveSchedule(eq(scheduleId), any(), any(ScheduleMoveCommand.class)))
                .willReturn(moveResult);

        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId))
                .andExpect(jsonPath("$.beforeDayId").value(moveResult.getBeforeDayId()))
                .andExpect(jsonPath("$.afterDayId").value(moveResult.getAfterDayId()))
                .andExpect(jsonPath("$.positionChanged").value(moveResult.isPositionChanged()));

        verify(scheduleMoveCommandFactory).createCommand(eq(targetDayId), eq(targetOrder));
        verify(scheduleMoveUseCase).moveSchedule(eq(scheduleId), any(), any(ScheduleMoveCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateSchedulePlace_with_unauthorizedUser() throws Exception {
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        ScheduleMoveRequest request = new ScheduleMoveRequest(targetDayId, targetOrder);

        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
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
    public void moveSchedule_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        String emptyContent = "";
        Long scheduleId = 1L;

        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
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
    public void moveSchedule_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();

        Long scheduleId = 1L;
        String invalidContent = """
                {
                    "targetDayId": 따옴표로 감싸지 않은 값,
                    "targetOrder": 123
                }
                """;

        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
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

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void moveSchedule_with_invalidType() throws Exception {
        mockingForLoginUserAnnotation();
        Long scheduleId = 1L;
        String invalidTypeContent = """
                {
                    "targetDayId": 1,
                    "targetOrder": "숫자가 아닌 값"
                }
                """;

        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidTypeContent)
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
