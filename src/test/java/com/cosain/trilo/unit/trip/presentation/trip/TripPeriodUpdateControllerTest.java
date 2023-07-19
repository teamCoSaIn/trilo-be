package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.presentation.trip.TripPeriodUpdateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 기간 수정 API 테스트")
@WebMvcTest(TripPeriodUpdateController.class)
class TripPeriodUpdateControllerTest extends RestControllerTest {

    @MockBean
    private TripPeriodUpdateService tripPeriodUpdateService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripPeriod_with_authorizedUser() throws Exception {
        // given
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;

        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
        TripPeriodUpdateCommand command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);

        willDoNothing().given(tripPeriodUpdateService).updateTripPeriod(eq(command));

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));
        verify(tripPeriodUpdateService, times(1)).updateTripPeriod(eq(command));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void updateTripPeriod_with_unauthorizedUser() throws Exception {
        // given
        Long tripId = 1L;

        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);

        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());


        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class));
    }

    @Test
    @DisplayName("tripId로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateTripPeriod_with_notNumberTripId() throws Exception {
        // given
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String notNumberTripId = "가가가";
        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
        mockMvc.perform(put("/api/trips/{tripId}/period", notNumberTripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class));
    }

    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_emptyContent() throws Exception {
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        String emptyContent = "";

        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(emptyContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class));
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidContent() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        String invalidContent = """
                {
                    "startDate" + 2023-03-01,
                    "endDate": "2023-03-02"
                }
                """;

        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class));
    }

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidType() throws Exception {
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        String invalidTypeContent = """
                {
                    "title": "제목",
                    "startDate": "2023-03-01",
                    "endDate": "날짜형식이 아닌 문자열"
                }
                """;

        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidTypeContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(any(TripPeriodUpdateCommand.class));
    }
}
