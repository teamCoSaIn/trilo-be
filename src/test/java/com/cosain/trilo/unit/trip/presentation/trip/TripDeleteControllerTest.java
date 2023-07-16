package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.TripDeleteService;
import com.cosain.trilo.trip.presentation.trip.TripDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 삭제 API 테스트")
@WebMvcTest(TripDeleteController.class)
class TripDeleteControllerTest extends RestControllerTest {
    @MockBean
    private TripDeleteService tripDeleteService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 올바른 여행 삭제 요청 -> 성공")
    public void deleteTrip_with_authorizedUser() throws Exception {
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        willDoNothing().given(tripDeleteService).deleteTrip(eq(tripId), any());

        mockMvc.perform(delete("/api/trips/" + tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
        verify(tripDeleteService).deleteTrip(eq(tripId), any());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void deleteTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/trips/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void deleteTrip_with_stringTripId() throws Exception {
        mockingForLoginUserAnnotation();

        mockMvc.perform(delete("/api/trips/가가가")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
