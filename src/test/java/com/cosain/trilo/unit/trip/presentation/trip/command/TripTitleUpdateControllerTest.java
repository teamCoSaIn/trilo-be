package com.cosain.trilo.unit.trip.presentation.trip.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.command.service.TripTitleUpdateService;
import com.cosain.trilo.trip.application.trip.dto.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripTitleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.TripTitleUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripTitleUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 제목 변경 API 문서화 테스트")
@WebMvcTest(TripTitleUpdateController.class)
public class TripTitleUpdateControllerTest extends RestControllerTest {

    @MockBean
    private TripTitleUpdateService tripTitleUpdateService;

    @MockBean
    private TripTitleUpdateCommandFactory tripTitleUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripTitle_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String rawTitle = "변경할 제목";

        TripTitleUpdateRequest request = new TripTitleUpdateRequest(rawTitle);

        given(tripTitleUpdateCommandFactory.createCommand(eq(rawTitle)))
                .willReturn(new TripTitleUpdateCommand(TripTitle.of(rawTitle)));
        willDoNothing().given(tripTitleUpdateService).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));


        mockMvc.perform(put("/api/trips/{tripId}/title", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));

        verify(tripTitleUpdateService, times(1)).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));
        verify(tripTitleUpdateCommandFactory, times(1)).createCommand(eq(rawTitle));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateTripTitle_with_unauthorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String rawTitle = "변경할 제목";

        TripTitleUpdateRequest request = new TripTitleUpdateRequest(rawTitle);

        mockMvc.perform(put("/api/trips/{tripId}/title", tripId)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripTitleUpdateService, times(0)).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));
        verify(tripTitleUpdateCommandFactory, times(0)).createCommand(eq(rawTitle));
    }

    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateTripTitle_with_notNumberTripId() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        String notNumberTripId = "가가가";
        String rawTitle = "변경할 제목";

        TripTitleUpdateRequest request = new TripTitleUpdateRequest(rawTitle);

        mockMvc.perform(put("/api/trips/{tripId}/title", notNumberTripId)
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
    }

    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTripTitle_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();
        Long tripId = 1L;

        String emptyContent = "";

        mockMvc.perform(put("/api/trips/{tripId}/title", tripId)
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

        verify(tripTitleUpdateService, times(0)).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));
        verify(tripTitleUpdateCommandFactory, times(0)).createCommand(any());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTrip_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String invalidContent = """
                {
                    "title": 괄호로 감싸지 않은 제목,
                }
                """;

        mockMvc.perform(put("/api/trips/1/title", tripId)
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


        verify(tripTitleUpdateService, times(0)).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));
        verify(tripTitleUpdateCommandFactory, times(0)).createCommand(any());
    }

}
