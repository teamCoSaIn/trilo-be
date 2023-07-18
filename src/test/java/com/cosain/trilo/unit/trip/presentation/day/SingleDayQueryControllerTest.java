package com.cosain.trilo.unit.trip.presentation.day;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.day.service.day_search.DaySearchService;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.day.SingleDayQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Day 단건 조회 API 테스트")
@WebMvcTest(SingleDayQueryController.class)
class SingleDayQueryControllerTest extends RestControllerTest {

    @MockBean
    private DaySearchService daySearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    @DisplayName("인증된 사용자 요청 -> Day 단건 조회")
    public void findSingleSchedule_with_authorizedUser() throws Exception {

        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(1L, "제목", "장소 이름","장소 식별자1", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(2L, "제목2", "장소 이름2","장소 식별자2", 33.33, 33.33);

        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(1L, 1L, LocalDate.of(2023, 2, 3), DayColor.RED, List.of(scheduleSummary1, scheduleSummary2));
        given(daySearchService.searchDaySchedule(eq(1L))).willReturn(dayScheduleDetail);

        mockMvc.perform(get("/api/days/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(dayScheduleDetail.getDayId()))
                .andExpect(jsonPath("$.date").value(dayScheduleDetail.getDate().toString()))
                .andExpect(jsonPath("$.tripId").value(dayScheduleDetail.getTripId()))
                .andExpect(jsonPath("$.schedules").isArray())
                .andExpect(jsonPath("$.schedules[0].coordinate.latitude").value(scheduleSummary1.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.schedules[0].coordinate.longitude").value(scheduleSummary1.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.schedules[0].scheduleId").value(scheduleSummary1.getScheduleId()))
                .andExpect(jsonPath("$.schedules[0].title").value(scheduleSummary1.getTitle()))
                .andExpect(jsonPath("$.schedules[0].placeName").value(scheduleSummary1.getPlaceName()))
                .andExpect(jsonPath("$.schedules[0].placeId").value(scheduleSummary1.getPlaceId()))
                .andExpect(jsonPath("$.schedules[1].coordinate.latitude").value(scheduleSummary2.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.schedules[1].coordinate.longitude").value(scheduleSummary2.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.schedules[1].scheduleId").value(scheduleSummary2.getScheduleId()))
                .andExpect(jsonPath("$.schedules[1].title").value(scheduleSummary2.getTitle()))
                .andExpect(jsonPath("$.schedules[1].placeName").value(scheduleSummary2.getPlaceName()))
                .andExpect(jsonPath("$.schedules[1].placeId").value(scheduleSummary2.getPlaceId()));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 200")
    public void findSingleSchedule_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/days/1"))
                .andExpect(status().isOk());
    }

}
