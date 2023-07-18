package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.presentation.trip.TripPeriodUpdateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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

    @MockBean
    private TripPeriodUpdateCommandFactory tripPeriodUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripPeriod_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;

        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
        TripPeriodUpdateCommand command = new TripPeriodUpdateCommand(TripPeriod.of(startDate, endDate));

        given(tripPeriodUpdateCommandFactory.createCommand(eq(startDate), eq(endDate))).willReturn(command);
        willDoNothing().given(tripPeriodUpdateService).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));


        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));

        verify(tripPeriodUpdateService, times(1)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(1)).createCommand(eq(startDate), eq(endDate));
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


        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(0)).createCommand(eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("tripId로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateTripPeriod_with_notNumberTripId() throws Exception {
        // given
        mockingForLoginUserAnnotation();

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

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(anyLong(), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(0)).createCommand(eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

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

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(0)).createCommand(any(), any());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();

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

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(0)).createCommand(any(), any());
    }

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripPeriod_with_invalidType() throws Exception {
        mockingForLoginUserAnnotation();

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

        verify(tripPeriodUpdateService, times(0)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(0)).createCommand(any(), any());
    }
}
