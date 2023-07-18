package com.cosain.trilo.unit.trip.presentation.schedule;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetailSearchService;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.presentation.schedule.SingleScheduleQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("일정 단건 조회 API 테스트")
@WebMvcTest(SingleScheduleQueryController.class)
class SingleScheduleQueryControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleDetailSearchService scheduleDetailSearchService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 일정 단건 조회")
    public void findSingleSchedule_with_authorizedUser() throws Exception {

        Long scheduleId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleDetail scheduleDetail = new ScheduleDetail(scheduleId, 1L, "제목", "장소 이름", 23.23, 23.23, 1L, "내용", LocalTime.of(15, 0), LocalTime.of(15, 30));
        given(scheduleDetailSearchService.searchScheduleDetail(anyLong())).willReturn(scheduleDetail);

        mockMvc.perform(get("/api/schedules/{scheduleId}", scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleDetail.getScheduleId()))
                .andExpect(jsonPath("$.content").value(scheduleDetail.getContent()))
                .andExpect(jsonPath("$.title").value(scheduleDetail.getTitle()))
                .andExpect(jsonPath("$.dayId").value(scheduleDetail.getDayId()))
                .andExpect(jsonPath("$.coordinate.latitude").value(scheduleDetail.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.coordinate.longitude").value(scheduleDetail.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.scheduleTime.startTime").value(scheduleDetail.getScheduleTime().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                .andExpect(jsonPath("$.scheduleTime.endTime").value(scheduleDetail.getScheduleTime().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 200")
    public void findSingleSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/schedules/1"))
                .andExpect(status().isOk());
    }
}
