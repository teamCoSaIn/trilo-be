package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripConditionSearchService;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.presentation.trip.TripConditionSearchController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripConditionSearchController.class)
public class TripConditionSearchControllerTest extends RestControllerTest {

    @MockBean
    private TripConditionSearchService tripConditionSearchService;

    private static final String BASE_URL = "/api/trips";

    @Test
    void 여행_목록_정상_조회_200() throws Exception {

        // given
        int size = 5;
        String query = "제주";
        Long tripId = 1L;
        String imageURL = "https://.../image.jpg";
        TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageURL);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageURL);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripConditionSearchService.findBySearchConditions(any(TripSearchRequest.class))).willReturn(tripSearchResponse);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("sortType", String.valueOf(sortType))
                        .param("query", query)
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 정렬_기준_값을_보내지_않을_경우_200() throws Exception {

        // given
        int size = 5;
        String query = "제주";
        Long tripId = 1L;
        String imageURL = "https://.../image.jpg";
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageURL);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageURL);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripConditionSearchService.findBySearchConditions(any(TripSearchRequest.class))).willReturn(tripSearchResponse);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("query", query)
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 페이지_사이즈를_보내지_않을_경우_200() throws Exception {

        // given
        String query = "제주";
        Long tripId = 1L;
        String imageURL = "https://.../image.jpg";
        TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageURL);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageURL);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripConditionSearchService.findBySearchConditions(any(TripSearchRequest.class))).willReturn(tripSearchResponse);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("sortType", String.valueOf(sortType))
                        .param("query", query)
                        .param("tripId", String.valueOf(tripId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @ParameterizedTest
    @ValueSource(ints = {101, 2000, 30000})
    void 페이지_사이즈가_100을_초과하는_경우_400(int size) throws Exception {
        // given
        String query = "제주";
        Long tripId = 1L;
        String imageURL = "https://.../image.jpg";
        TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageURL);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageURL);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripConditionSearchService.findBySearchConditions(any(TripSearchRequest.class))).willReturn(tripSearchResponse);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("sortType", String.valueOf(sortType))
                        .param("query", query)
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

