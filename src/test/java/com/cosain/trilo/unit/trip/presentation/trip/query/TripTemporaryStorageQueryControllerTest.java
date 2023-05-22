package com.cosain.trilo.unit.trip.presentation.trip.query;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.query.service.dto.ScheduleResult;
import com.cosain.trilo.trip.application.trip.query.service.dto.TemporaryPageResult;
import com.cosain.trilo.trip.application.trip.query.service.TemporarySearchUseCase;
import com.cosain.trilo.trip.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.presentation.trip.query.TripTemporaryStorageQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    @WithMockUser
    public void findTripTemporaryStorage_with_authorizedUser() throws Exception {

        // given
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        TemporaryPageResult temporaryPageResult = TemporaryPageResult.of(List.of(
                ScheduleResult.from(ScheduleDto.from(new ScheduleDetail(1L, null, "제목", "장소이름", 33.33, 33.33, 1L, "내용"))),
                ScheduleResult.from(ScheduleDto.from(new ScheduleDetail(2L, null, "제목", "장소이름", 33.33, 33.33, 1L, "내용"))),
                ScheduleResult.from(ScheduleDto.from(new ScheduleDetail(3L, null, "제목", "장소이름", 33.33, 33.33, 1L, "내용"))),
                ScheduleResult.from(ScheduleDto.from(new ScheduleDetail(4L, null, "제목", "장소이름", 33.33, 33.33, 1L, "내용")))),

                true);
        given(temporarySearchUseCase.searchTemporary(eq(tripId), any(Pageable.class))).willReturn(temporaryPageResult);

        mockMvc.perform(get("/api/trips/1/temporary-storage")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tempSchedules").isArray())
                .andExpect(jsonPath("$.tempSchedules.size()").value(temporaryPageResult.getScheduleResults().size()))
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
