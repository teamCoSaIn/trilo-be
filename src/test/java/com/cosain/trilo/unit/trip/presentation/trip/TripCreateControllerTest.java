package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateService;
import com.cosain.trilo.trip.presentation.trip.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 생성 API 테스트")
@WebMvcTest(TripCreateController.class)
class TripCreateControllerTest extends RestControllerTest {

    @MockBean
    private TripCreateService tripCreateService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 여행 생성 요청 -> 성공")
    public void successTest() throws Exception {
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String rawTitle = "제목";
        TripCreateRequest request = new TripCreateRequest(rawTitle);

        Long tripId = 1L;
        TripCreateCommand command = TripCreateCommand.of(tripperId, rawTitle);
        given(tripCreateService.createTrip(eq(command))).willReturn(tripId);

        // when
        ResultActions resultActions = runTest(createJson(request));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(tripId));
        verify(tripCreateService, times(1)).createTrip(eq(command));
    }

    @Test
    @DisplayName("토큰이 없는 사용자 요청 -> 인증 실패 401")
    public void createTrip_without_token() throws Exception {
        // given
        String rawTitle = "제목";
        TripCreateRequest request = new TripCreateRequest(rawTitle);

        // when
        ResultActions resultActions = runTestWithoutAuthorization(createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_emptyContent() throws Exception {
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String emptyContent = "";

        // when
        ResultActions resultActions = runTest(emptyContent);

        // given
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }


    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_invalidContent() throws Exception {
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);
        String invalidContent = """
                {
                    "title": 따옴표 안 감싼 제목
                }
                """;

        // when
        ResultActions resultActions = runTest(invalidContent);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }

    @DisplayName("제목 null -> 검증예외 발생")
    @Test
    public void testNullTitle() throws Exception {
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String nullTitle = null;
        TripCreateRequest request = new TripCreateRequest(nullTitle);

        // when
        ResultActions resultActions = runTest(createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }

    @DisplayName("제목 빈문자열 -> 검증예외 발생")
    @Test
    public void emptyTitle() throws Exception {
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String emptyTitle = "";
        TripCreateRequest request = new TripCreateRequest(emptyTitle);

        // when
        ResultActions resultActions = runTest(createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }

    @DisplayName("제목 공백으로만 구성 -> 검증예외 발생")
    @Test
    public void whiteSpaceTitle() throws Exception {
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String emptyTitle = "     ";
        TripCreateRequest request = new TripCreateRequest(emptyTitle);

        // when
        ResultActions resultActions = runTest(createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }

    @DisplayName("20자보다 긴 제목 -> 검증예외 발생")
    @Test
    public void tooLongTitle() throws Exception{
        // given
        Long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        String emptyTitle = "가".repeat(21);
        TripCreateRequest request = new TripCreateRequest(emptyTitle);

        // when
        ResultActions resultActions = runTest(createJson(request));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
        verify(tripCreateService, times(0)).createTrip(any(TripCreateCommand.class));
    }

    private ResultActions runTest(String content) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions runTestWithoutAuthorization(String content) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
