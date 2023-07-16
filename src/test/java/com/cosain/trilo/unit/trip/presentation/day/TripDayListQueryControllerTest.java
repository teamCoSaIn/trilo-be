package com.cosain.trilo.unit.trip.presentation.day;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.day.service.DaySearchService;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.day.TripDayListQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행의 Day 목록 조회 API 테스트")
@WebMvcTest(TripDayListQueryController.class)
class TripDayListQueryControllerTest extends RestControllerTest {


    @MockBean
    private DaySearchService daySearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("Day 목록 조회 -> 성공")
    public void findTripDayList_with_authorizedUser() throws Exception {

        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary = new ScheduleSummary(1L, "제목", "장소 이름", "장소 식별자", 33.33, 33.33);
        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(1L, 1L, LocalDate.of(2023, 5, 13), DayColor.BLACK, List.of(scheduleSummary));
        List<DayScheduleDetail> dayScheduleDetails = List.of(dayScheduleDetail);

        given(daySearchService.searchDaySchedules(tripId)).willReturn(dayScheduleDetails);

        mockMvc.perform(get("/api/trips/{tripId}/days", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.days[0].dayId").isNumber())
                .andExpect(jsonPath("$.days[0].tripId").isNumber())
                .andExpect(jsonPath("$.days[0].date").isString())
                .andExpect(jsonPath("$.days[0].schedules").isArray())
                .andExpect(jsonPath("$.days[0].schedules[0].scheduleId").isNumber())
                .andExpect(jsonPath("$.days[0].schedules[0].title").isString())
                .andExpect(jsonPath("$.days[0].schedules[0].placeName").isString())
                .andExpect(jsonPath("$.days[0].schedules[0].placeId").isString())
                .andExpect(jsonPath("$.days[0].schedules[0].coordinate.latitude").isNumber())
                .andExpect(jsonPath("$.days[0].schedules[0].coordinate.longitude").isNumber());

    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 200")
    public void findTripDayList_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips/1/days"))
                .andExpect(status().isOk());
    }
}
