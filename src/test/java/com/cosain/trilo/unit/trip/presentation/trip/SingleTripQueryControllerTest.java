package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetailSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.presentation.trip.SingleTripQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 단건 조회 API 테스트")
@WebMvcTest(SingleTripQueryController.class)
class SingleTripQueryControllerTest extends RestControllerTest {

    @MockBean
    private TripDetailSearchService tripDetailSearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 요청 -> 여행 단건 정보 조회")
    public void findSingleTrip_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();
        TripDetail tripDetail = new TripDetail(1L, 2L, "여행 제목", TripStatus.DECIDED, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 5));
        given(tripDetailSearchService.searchTripDetail(anyLong())).willReturn(tripDetail);

        // when & then
        mockMvc.perform(get("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripDetail.getTripId()))
                .andExpect(jsonPath("$.title").value(tripDetail.getTitle()))
                .andExpect(jsonPath("$.status").value(tripDetail.getStatus()))
                .andExpect(jsonPath("$.startDate").value(tripDetail.getStartDate().toString()))
                .andExpect(jsonPath("$.endDate").value(tripDetail.getEndDate().toString()));


        verify(tripDetailSearchService).searchTripDetail(anyLong());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 200")
    public void findSingleTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
