package com.cosain.trilo.unit.trip.presentation.trip;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.presentation.trip.TripperTripListQueryController;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("특정 사용자 여행 목록 API 테스트")
@WebMvcTest(TripperTripListQueryController.class)
class TripperTripListQueryControllerTest extends RestControllerTest {

    @MockBean
    private TripListSearchService tripListSearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 회원 여행 목록 조회")
    public void findTripperTripList_with_authorizedUser() throws Exception {

        // given
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Integer size = 3;

        TripListSearchResult.TripSummary tripSummary1 = new TripListSearchResult.TripSummary(1L, tripperId, "제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3,4), LocalDate.of(2023, 3, 5), "image.jpg");
        TripListSearchResult.TripSummary tripSummary2 = new TripListSearchResult.TripSummary(2L, tripperId, "제목 2", TripStatus.UNDECIDED, null, null, "image.jpg");
        TripListSearchResult.TripSummary tripSummary3 = new TripListSearchResult.TripSummary(3L, tripperId, "제목 3", TripStatus.DECIDED, LocalDate.of(2023, 4,4), LocalDate.of(2023, 4, 5), "image.jpg");

        TripListQueryParam queryParam = TripListQueryParam.of(tripperId, null, size);
        TripListSearchResult searchResult = TripListSearchResult.of(true, List.of(tripSummary3, tripSummary2, tripSummary1));

        given(tripListSearchService.searchTripList(eq(queryParam))).willReturn(searchResult);

        // when & then
        mockMvc.perform(get("/api/tripper/{tripperId}/trips", tripperId)
                        .param("size", String.valueOf(size))
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(searchResult.isHasNext()))
                .andExpect(jsonPath("$.trips").isNotEmpty())
                .andExpect(jsonPath("$.trips.[0].tripId").value(tripSummary3.getTripId()))
                .andExpect(jsonPath("$.trips.[1].tripId").value(tripSummary2.getTripId()))
                .andExpect(jsonPath("$.trips.[2].tripId").value(tripSummary1.getTripId()))
                .andExpect(jsonPath("$.trips.[0].title").value(tripSummary3.getTitle()))
                .andExpect(jsonPath("$.trips.[1].title").value(tripSummary2.getTitle()))
                .andExpect(jsonPath("$.trips.[2].title").value(tripSummary1.getTitle()))
                .andExpect(jsonPath("$.trips.[0].status").value(tripSummary3.getStatus()))
                .andExpect(jsonPath("$.trips.[1].status").value(tripSummary2.getStatus()))
                .andExpect(jsonPath("$.trips.[2].status").value(tripSummary1.getStatus()))
                .andExpect(jsonPath("$.trips.[0].startDate").value(tripSummary3.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[1].startDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].startDate").value(tripSummary1.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[0].endDate").value(tripSummary3.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[1].endDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].endDate").value(tripSummary1.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[0].imageURL").value(tripSummary3.getImageURL()))
                .andExpect(jsonPath("$.trips.[1].imageURL").value(tripSummary2.getImageURL()))
                .andExpect(jsonPath("$.trips.[2].imageURL").value(tripSummary1.getImageURL()));
    }

    @Test
    @DisplayName("로그인 하지 않은 사용자 요청 -> 200 조회 성공")
    public void findTripperTripList_with_unauthorizedUser() throws Exception {
        // given
        long tripperId = 1L;
        Integer size = 3;

        TripListSearchResult.TripSummary tripSummary1 = new TripListSearchResult.TripSummary(1L, tripperId, "제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3,4), LocalDate.of(2023, 3, 5), "image.jpg");
        TripListSearchResult.TripSummary tripSummary2 = new TripListSearchResult.TripSummary(2L, tripperId, "제목 2", TripStatus.UNDECIDED, null, null, "image.jpg");
        TripListSearchResult.TripSummary tripSummary3 = new TripListSearchResult.TripSummary(3L, tripperId, "제목 3", TripStatus.DECIDED, LocalDate.of(2023, 4,4), LocalDate.of(2023, 4, 5), "image.jpg");

        TripListQueryParam queryParam = TripListQueryParam.of(tripperId, null, size);
        TripListSearchResult searchResult = TripListSearchResult.of(true, List.of(tripSummary3, tripSummary2, tripSummary1));

        given(tripListSearchService.searchTripList(eq(queryParam))).willReturn(searchResult);

        // when & then
        mockMvc.perform(get("/api/tripper/{tripperId}/trips", tripperId)
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(searchResult.isHasNext()))
                .andExpect(jsonPath("$.trips").isNotEmpty())
                .andExpect(jsonPath("$.trips.[0].tripId").value(tripSummary3.getTripId()))
                .andExpect(jsonPath("$.trips.[1].tripId").value(tripSummary2.getTripId()))
                .andExpect(jsonPath("$.trips.[2].tripId").value(tripSummary1.getTripId()))
                .andExpect(jsonPath("$.trips.[0].title").value(tripSummary3.getTitle()))
                .andExpect(jsonPath("$.trips.[1].title").value(tripSummary2.getTitle()))
                .andExpect(jsonPath("$.trips.[2].title").value(tripSummary1.getTitle()))
                .andExpect(jsonPath("$.trips.[0].status").value(tripSummary3.getStatus()))
                .andExpect(jsonPath("$.trips.[1].status").value(tripSummary2.getStatus()))
                .andExpect(jsonPath("$.trips.[2].status").value(tripSummary1.getStatus()))
                .andExpect(jsonPath("$.trips.[0].startDate").value(tripSummary3.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[1].startDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].startDate").value(tripSummary1.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[0].endDate").value(tripSummary3.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[1].endDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].endDate").value(tripSummary1.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[0].imageURL").value(tripSummary3.getImageURL()))
                .andExpect(jsonPath("$.trips.[1].imageURL").value(tripSummary2.getImageURL()))
                .andExpect(jsonPath("$.trips.[2].imageURL").value(tripSummary1.getImageURL()));
    }

}
