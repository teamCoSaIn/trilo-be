package com.cosain.trilo.unit.trip.presentation.trip.query;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.query.service.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.query.TripperTripListQueryController;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        mockingForLoginUserAnnotation();

        long tripperId = 1L;

        TripSummary tripSummary1 = new TripSummary(1L, tripperId, "제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3,4), LocalDate.of(2023, 4, 1), "image.jpg");
        TripSummary tripSummary2 = new TripSummary(2L, tripperId, "제목 2", TripStatus.UNDECIDED, null, null, "image.jpg");
        TripSummary tripSummary3 = new TripSummary(3L, tripperId, "제목 3", TripStatus.DECIDED, LocalDate.of(2023, 4,4), LocalDate.of(2023, 4, 5), "image.jpg");
        Pageable pageable = PageRequest.of(0, 3);

        SliceImpl<TripSummary> tripDetails = new SliceImpl<>(List.of(tripSummary3, tripSummary2, tripSummary1), pageable, true);

        given(tripListSearchService.searchTripSummaries(any(TripPageCondition.class), any(Pageable.class))).willReturn(tripDetails);

        // when & then
        mockMvc.perform(get("/api/trips?tripper-id={tripperId}", tripperId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.trips").isNotEmpty())
                .andExpect(jsonPath("$.trips.[*].tripId").exists())
                .andExpect(jsonPath("$.trips.[*].tripperId").exists())
                .andExpect(jsonPath("$.trips.[*].title").exists())
                .andExpect(jsonPath("$.trips.[*].status").exists())
                .andExpect(jsonPath("$.trips.[*].startDate").exists())
                .andExpect(jsonPath("$.trips.[*].endDate").exists())
                .andExpect(jsonPath("$.trips.[*].imageURL").exists());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findTripperTripList_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips?tripper-id=1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

}
