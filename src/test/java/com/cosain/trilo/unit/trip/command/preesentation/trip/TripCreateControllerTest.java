package com.cosain.trilo.unit.trip.command.preesentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.command.application.usecase.TripCreateUseCase;
import com.cosain.trilo.trip.command.presentation.trip.TripCreateController;
import com.cosain.trilo.trip.command.presentation.trip.dto.TripCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 생성 API 테스트")
@WebMvcTest(TripCreateController.class)
class TripCreateControllerTest extends RestControllerTest {

    @MockBean
    private TripCreateUseCase tripCreateUseCase;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 여행 생성 요청 -> 성공")
    public void successTest() throws Exception{
        mockingForLoginUserAnnotation();
        TripCreateRequest request = new TripCreateRequest("제목");
        given(tripCreateUseCase.createTrip(any(), any())).willReturn(1L);

        mockMvc.perform(post("/api/trips")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(1L));

        verify(tripCreateUseCase).createTrip(any(), any());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void createTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/trips"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }
}

