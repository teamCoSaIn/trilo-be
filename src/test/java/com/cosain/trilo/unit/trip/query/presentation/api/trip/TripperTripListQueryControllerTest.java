package com.cosain.trilo.unit.trip.query.presentation.api.trip;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import com.cosain.trilo.trip.query.application.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.query.domain.dto.TripDto;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.presentation.trip.TripperTripListQueryController;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    private TripListSearchUseCase tripListSearchUseCase;
    private final String ACCESS_TOKEN = "Bearer accessToken";


    @Test
    @DisplayName("인증된 사용자 요청 -> 회원 여행 목록 조회")
    public void findTripperTripList_with_authorizedUser() throws Exception {

        mockingForLoginUserAnnotation();

        TripDetailResponse dto1 = TripDetailResponse.from(TripDto.from(new TripDetail(1L, 1L, "여행 제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3, 4), LocalDate.of(2023, 4, 1))));
        TripDetailResponse dto2 = TripDetailResponse.from(TripDto.from(new TripDetail(23L, 1L, "여행 제목 2", TripStatus.DECIDED, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 5, 1))));
        TripDetailResponse dto3 = TripDetailResponse.from(TripDto.from(new TripDetail(43L, 1L, "여행 제목 3", TripStatus.DECIDED, LocalDate.of(2023, 5, 4), LocalDate.of(2023, 6, 1))));
        TripPageResponse tripPageResponse = TripPageResponse.of(List.of(dto1, dto2, dto3), true);
        given(tripListSearchUseCase.searchTripDetails(eq(1L), any(Pageable.class))).willReturn(tripPageResponse);

        mockMvc.perform(get("/api/trips?tripper-id=1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.trips").isNotEmpty());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findTripperTripList_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips?tripper-id=1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

}
