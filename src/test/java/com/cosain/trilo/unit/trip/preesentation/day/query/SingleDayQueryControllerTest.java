package com.cosain.trilo.unit.trip.preesentation.day.query;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.day.query.service.DaySearchUseCase;
import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.presentation.day.query.SingleDayQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Day 단건 조회 API 테스트")
@WebMvcTest(SingleDayQueryController.class)
class SingleDayQueryControllerTest extends RestControllerTest {

    @MockBean
    private DaySearchUseCase daySearchUseCase;
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    @DisplayName("인증된 사용자 요청 -> Day 단건 조회")
    public void findSingleSchedule_with_authorizedUser() throws Exception {

        mockingForLoginUserAnnotation();
        DayScheduleDetail.ScheduleSummary scheduleSummary1 = new DayScheduleDetail.ScheduleSummary(1L, "제목", "장소 이름", 33.33, 33.33);
        DayScheduleDetail.ScheduleSummary scheduleSummary2 = new DayScheduleDetail.ScheduleSummary(2L, "제목2", "장소 이름2", 33.33, 33.33);

        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(1L, 1L, LocalDate.of(2023, 2, 3), List.of(scheduleSummary1, scheduleSummary2));
        given(daySearchUseCase.searchDeySchedule(eq(1L))).willReturn(dayScheduleDetail);

        mockMvc.perform(get("/api/days/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(dayScheduleDetail.getDayId()))
                .andExpect(jsonPath("$.date").value(dayScheduleDetail.getDate().toString()))
                .andExpect(jsonPath("$.tripId").value(dayScheduleDetail.getTripId()))
                .andExpect(jsonPath("$.scheduleSummaries").isArray());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findSingleSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/days/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

}
