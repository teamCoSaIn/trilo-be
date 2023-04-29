package com.cosain.trilo.unit.trip.command.preesentation.api.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.command.application.usecase.TripDeleteUseCase;
import com.cosain.trilo.trip.command.presentation.trip.TripDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[TripCommand] 여행 삭제 API 테스트")
@WebMvcTest(TripDeleteController.class)
class TripDeleteControllerTest extends RestControllerTest {

    @MockBean
    private TripDeleteUseCase tripDeleteUseCase;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 올바른 여행 삭제 요청 -> 미구현 500")
    public void deleteTrip_with_authorizedUser() throws Exception {
        mockingForLoginUserAnnotation();
        willDoNothing().given(tripDeleteUseCase).deleteTrip(eq(1L), any());

        mockMvc.perform(delete("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
        verify(tripDeleteUseCase).deleteTrip(eq(1L), any());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void deleteTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/trips/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

}
