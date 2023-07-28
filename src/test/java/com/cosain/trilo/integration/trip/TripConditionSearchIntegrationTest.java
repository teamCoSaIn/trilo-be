package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 여행 목록 조회 API 테스트")
public class TripConditionSearchIntegrationTest extends IntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    private static final String BASE_URL = "/api/trips";

    @Nested
    class 여행_목록_조회{
        @Test
        void 커서_없이_조회() throws Exception{

            // given
            User user = setupMockKakaoUser();
            setupMockTrip("제천 가자", user.getId());
            setupMockTrip("강원도 여행", user.getId());
            setupMockTrip("제주도 여행", user.getId());
            setupMockTrip("제주 1박 2일 여행", user.getId());

            int size = 5;
            String query = "제주";
            TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                            .param("sortType", String.valueOf(sortType))
                            .param("query", query)
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.trips.size()").value(2))
                    .andExpect(jsonPath("$.trips[0].title").value("제주 1박 2일 여행"))
                    .andExpect(jsonPath("$.trips[1].title").value("제주도 여행"));
        }

        @Test
        void 여행_ID_커서_포함_조회() throws Exception{

            // given
            User user = setupMockKakaoUser();
            setupMockTrip("제천 가자", user.getId());
            setupMockTrip("강원도 여행", user.getId());
            setupMockTrip("제주도 여행", user.getId());
            Trip trip = setupMockTrip("제주 1박 2일 여행", user.getId());

            int size = 5;
            String query = "제주";
            Long tripId = trip.getId();
            TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                            .param("sortType", String.valueOf(sortType))
                            .param("query", query)
                            .param("tripId", String.valueOf(tripId))
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.trips.size()").value(1))
                    .andExpect(jsonPath("$.trips[0].title").value("제주도 여행"));
        }

    }

    private Trip setupMockTrip(String rawTitle, Long tripperId) {
        Trip trip = TripFixture.decided_nullId_Title(tripperId, rawTitle, LocalDate.of(2023, 5, 15), LocalDate.of(2023, 5, 20));
        tripRepository.save(trip);
        return trip;
    }
}
