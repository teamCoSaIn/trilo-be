package com.cosain.trilo.unit.trip.query.presentation.api.schedule;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.query.application.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.query.presentation.schedule.SingleScheduleQueryController;
import com.cosain.trilo.trip.query.presentation.schedule.dto.ScheduleDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("일정 단건 조회 API 테스트")
@WebMvcTest(SingleScheduleQueryController.class)
class SingleScheduleQueryControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleDetailSearchUseCase scheduleDetailSearchUseCase;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 일정 단건 조회")
    public void findSingleSchedule_with_authorizedUser() throws Exception {

        mockingForLoginUserAnnotation();
        ScheduleDetailResponse response = ScheduleDetailResponse.of(1L, 1L, "제목", "여행장소", 12.12, 13.13, 1L, "내용");
        given(scheduleDetailSearchUseCase.searchScheduleDetail(anyLong())).willReturn(response);

        mockMvc.perform(get("/api/schedules/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(response.getScheduleId()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.dayId").value(response.getDayId()))
                .andExpect(jsonPath("$.latitude").value(response.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(response.getLongitude()));

    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findSingleSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/schedules/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }
}
