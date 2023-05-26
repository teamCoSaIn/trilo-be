package com.cosain.trilo.unit.trip.presentation.trip.query;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.query.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.trip.query.TripTemporaryStorageQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행의 임시보관함 조회 API 테스트")
@WebMvcTest(TripTemporaryStorageQueryController.class)
class TripTemporaryStorageQueryControllerTest extends RestControllerTest {

    @MockBean
    private TemporarySearchUseCase temporarySearchUseCase;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("정상 동작 확인")
    public void findTripTemporaryStorage_with_authorizedUser() throws Exception {

        // given
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(1L, null, "제목", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(2L, null, "제목", 33.33, 33.33);
        SliceImpl<ScheduleSummary> scheduleSummaries = new SliceImpl<>(List.of(scheduleSummary1, scheduleSummary2));
        given(temporarySearchUseCase.searchTemporary(eq(tripId), any(Pageable.class))).willReturn(scheduleSummaries);

        mockMvc.perform(get("/api/trips/1/temporary-storage")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tempSchedules").isArray())
                .andExpect(jsonPath("$.tempSchedules.size()").value(scheduleSummaries.getSize()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.latitude").value(scheduleSummary1.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.longitude").value(scheduleSummary1.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.tempSchedules[0].scheduleId").value(scheduleSummary1.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[0].title").value(scheduleSummary1.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[0].placeName").value(scheduleSummary1.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.latitude").value(scheduleSummary2.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.longitude").value(scheduleSummary2.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.tempSchedules[1].scheduleId").value(scheduleSummary2.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[1].title").value(scheduleSummary2.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[1].placeName").value(scheduleSummary2.getPlaceName()))
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findTripTemporaryStorage_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips/1/temporary-storage"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
