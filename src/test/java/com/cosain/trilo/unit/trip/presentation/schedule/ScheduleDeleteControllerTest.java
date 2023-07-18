package com.cosain.trilo.unit.trip.presentation.schedule;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.service.ScheduleDeleteService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("일정 삭제 API 테스트")
@WebMvcTest(ScheduleDeleteController.class)
class ScheduleDeleteControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleDeleteService scheduleDeleteService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void deleteSchedule_with_authorizedUser() throws Exception {
        mockingForLoginUserAnnotation();
        willDoNothing().given(scheduleDeleteService).deleteSchedule(eq(1L), any());

        mockMvc.perform(delete("/api/schedules/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
        verify(scheduleDeleteService).deleteSchedule(eq(1L), any());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void deleteSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/schedules/1")
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
