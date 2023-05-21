package com.cosain.trilo.unit.trip.preesentation.schedule.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleUpdateUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private ScheduleUpdateUseCase scheduleUpdateUseCase;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 올바른 요청 -> 일정 수정됨")
    public void updateSchedule_with_authorizedUser() throws Exception {

        // given
        mockingForLoginUserAnnotation();
        ScheduleUpdateRequest scheduleUpdateRequest = new ScheduleUpdateRequest("수정할 제목", "수정할 내용");

        given(scheduleUpdateUseCase.updateSchedule(anyLong(),any(),any(ScheduleUpdateCommand.class))).willReturn(1L);

        // when & then
        mockMvc.perform(put("/api/schedules/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(scheduleUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1L))
                .andDo(print());

        verify(scheduleUpdateUseCase).updateSchedule(anyLong(),any(),any(ScheduleUpdateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(put("/api/schedules/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

}
