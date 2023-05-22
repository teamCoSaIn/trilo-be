package com.cosain.trilo.unit.trip.presentation.trip.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.presentation.trip.command.TripUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 수정 API 테스트")
@WebMvcTest(TripUpdateController.class)
class TripUpdateControllerTest extends RestControllerTest {

    @MockBean
    private TripUpdateUseCase tripUpdateUseCase;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTrip_with_authorizedUser() throws Exception {

        mockingForLoginUserAnnotation();
        TripUpdateRequest request = new TripUpdateRequest("변경할 제목", LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 15));

        mockMvc.perform(put("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedTripId").value(1L));

        verify(tripUpdateUseCase).updateTrip(any(), any(), any(TripUpdateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(put("/api/trips"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

}
