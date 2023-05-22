package com.cosain.trilo.unit.trip.presentation.trip.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.command.usecase.TripCreateUseCase;
import com.cosain.trilo.trip.presentation.trip.command.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripCreateRequest;
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
    public void successTest() throws Exception {
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
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        String emptyContent = "";

        mockMvc.perform(post("/api/trips")
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
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidContent = """
                {
                    "title": 따옴표 안 감싼 제목
                }
                """;

        mockMvc.perform(post("/api/trips")
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
    }

    @Test
    @DisplayName("제목이 null -> 필드 검증 실패 -> 400 예외")
    public void createTrip_with_nullTitle() throws Exception {
        mockingForLoginUserAnnotation();
        TripCreateRequest request = new TripCreateRequest(null);

        mockMvc.perform(post("/api/trips")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors[*].errorCode").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorMessage").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors[*].field").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title' && @.errorCode=='trip-0002')]").exists())
                .andExpect(jsonPath("$.globalErrors").exists())
                .andExpect(jsonPath("$.globalErrors").isEmpty());
    }

    @Test
    @DisplayName("제목이 빈 문자열 -> 필드 검증 실패 -> 400 예외")
    public void createTrip_with_emptyTitle() throws Exception {
        mockingForLoginUserAnnotation();
        TripCreateRequest request = new TripCreateRequest("");

        mockMvc.perform(post("/api/trips")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors[*].errorCode").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorMessage").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors[*].field").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title' && @.errorCode=='trip-0002')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title' && @.errorCode=='trip-0003')]").exists())
                .andExpect(jsonPath("$.globalErrors").exists())
                .andExpect(jsonPath("$.globalErrors").isEmpty());
    }

    @Test
    @DisplayName("제목이 공백만으로 구성된 문자열 -> 필드 검증 실패 -> 400 예외")
    public void createTrip_with_whiteSpaceTitle() throws Exception {
        mockingForLoginUserAnnotation();
        TripCreateRequest request = new TripCreateRequest("     ");

        mockMvc.perform(post("/api/trips")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors[*].errorCode").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorMessage").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors[*].field").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title' && @.errorCode=='trip-0002')]").exists())
                .andExpect(jsonPath("$.globalErrors").exists())
                .andExpect(jsonPath("$.globalErrors").isEmpty());
    }

    @Test
    @DisplayName("제목이 20자를 넘어갈 떄 -> 필드 검증 실패 -> 400 예외")
    public void createTrip_with_tooLongTitle() throws Exception {
        mockingForLoginUserAnnotation();
        TripCreateRequest request = new TripCreateRequest("가".repeat(21));

        mockMvc.perform(post("/api/trips")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors[*].errorCode").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorMessage").exists())
                .andExpect(jsonPath("$.fieldErrors[*].errorDetail").exists())
                .andExpect(jsonPath("$.fieldErrors[*].field").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title' && @.errorCode=='trip-0003')]").exists())
                .andExpect(jsonPath("$.globalErrors").exists())
                .andExpect(jsonPath("$.globalErrors").isEmpty());
    }
}
