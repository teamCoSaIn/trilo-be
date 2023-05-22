package com.cosain.trilo.unit.trip.presentation.schedule.command;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleMoveUseCase;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[TripCommand] 일정 이동 API 테스트")
@WebMvcTest(ScheduleMoveController.class)
public class ScheduleMoveControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleMoveUseCase scheduleMoveUseCase;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

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
        given(scheduleMoveUseCase.moveSchedule(eq(scheduleId), any(), any(ScheduleMoveCommand.class)))
                .willReturn(moveResult);

        mockMvc.perform(patch("/api/schedules/" + scheduleId)
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

        mockMvc.perform(patch("/api/schedules/" + scheduleId)
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

}
