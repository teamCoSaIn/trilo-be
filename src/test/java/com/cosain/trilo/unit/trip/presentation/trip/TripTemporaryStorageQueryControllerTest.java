package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListSearchResult;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TemporarySearchService;
import com.cosain.trilo.trip.presentation.trip.TripTemporaryStorageQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행의 임시보관함 조회 API 테스트")
@WebMvcTest(TripTemporaryStorageQueryController.class)
class TripTemporaryStorageQueryControllerTest extends RestControllerTest {

    @MockBean
    private TemporarySearchService temporarySearchService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("정상 동작 확인")
    public void findTripTemporaryStorage_with_authorizedUser() throws Exception {
        // given
        int size = 2;
        Long tripId = 1L;
        Long scheduleId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(2L, "일정 제목1", "제목","장소 식별자", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(3L, "일정 제목2", "제목","장소 식별자",33.33, 33.33);

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        var result = TempScheduleListSearchResult.of(true, List.of(scheduleSummary1, scheduleSummary2));
        given(temporarySearchService.searchTemporary(eq(queryParam))).willReturn(result);

        mockMvc.perform(get("/api/trips/{tripId}/temporary-storage", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .param("size", String.valueOf(size))
                        .param("scheduleId", String.valueOf(scheduleId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(jsonPath("$.tempSchedules").isNotEmpty())
                .andExpect(jsonPath("$.tempSchedules.size()").value(result.getTempSchedules().size()))
                .andExpect(jsonPath("$.tempSchedules[0].scheduleId").value(scheduleSummary1.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[1].scheduleId").value(scheduleSummary2.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[0].title").value(scheduleSummary1.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[1].title").value(scheduleSummary2.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[0].placeId").value(scheduleSummary1.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[1].placeId").value(scheduleSummary2.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[0].placeName").value(scheduleSummary1.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[1].placeName").value(scheduleSummary2.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.latitude").value(scheduleSummary1.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.latitude").value(scheduleSummary2.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.longitude").value(scheduleSummary1.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.longitude").value(scheduleSummary2.getCoordinate().getLongitude()));

        verify(temporarySearchService, times(1)).searchTemporary(eq(queryParam));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 200 성공")
    public void findTripTemporaryStorage_with_unauthorizedUser() throws Exception {
        // given
        int size = 2;
        Long tripId = 1L;
        Long scheduleId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(2L, "일정 제목1", "제목","장소 식별자", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(3L, "일정 제목2", "제목","장소 식별자",33.33, 33.33);

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        var result = TempScheduleListSearchResult.of(true, List.of(scheduleSummary1, scheduleSummary2));
        given(temporarySearchService.searchTemporary(eq(queryParam))).willReturn(result);

        mockMvc.perform(get("/api/trips/{tripId}/temporary-storage", tripId)
                        .param("size", String.valueOf(size))
                        .param("scheduleId", String.valueOf(scheduleId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(jsonPath("$.tempSchedules").isNotEmpty())
                .andExpect(jsonPath("$.tempSchedules.size()").value(result.getTempSchedules().size()))
                .andExpect(jsonPath("$.tempSchedules[0].scheduleId").value(scheduleSummary1.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[1].scheduleId").value(scheduleSummary2.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[0].title").value(scheduleSummary1.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[1].title").value(scheduleSummary2.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[0].placeId").value(scheduleSummary1.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[1].placeId").value(scheduleSummary2.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[0].placeName").value(scheduleSummary1.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[1].placeName").value(scheduleSummary2.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.latitude").value(scheduleSummary1.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.latitude").value(scheduleSummary2.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.longitude").value(scheduleSummary1.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.longitude").value(scheduleSummary2.getCoordinate().getLongitude()));

        verify(temporarySearchService, times(1)).searchTemporary(eq(queryParam));
    }
}
