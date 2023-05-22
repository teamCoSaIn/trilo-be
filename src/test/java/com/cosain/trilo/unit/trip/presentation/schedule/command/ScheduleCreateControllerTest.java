package com.cosain.trilo.unit.trip.presentation.schedule.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleCreateController;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("일정 생성 API 테스트")
@WebMvcTest(ScheduleCreateController.class)
class ScheduleCreateControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleCreateUseCase scheduleCreateUseCase;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 올바른 요청 -> 일정 생성됨")
    @WithMockUser
    public void createSchedule_with_authorizedUser() throws Exception {
        mockingForLoginUserAnnotation();

        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .dayId(1L)
                .tripId(1L)
                .title("일정 제목")
                .placeId("google-place-id-1234")
                .placeName("장소명")
                .latitude(37.5642135)
                .longitude(127.0016985)
                .build();

        given(scheduleCreateUseCase.createSchedule(any(), any(ScheduleCreateCommand.class))).willReturn(1L);

        mockMvc.perform(post("/api/schedules")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").value(1L));

        verify(scheduleCreateUseCase).createSchedule(any(), any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void createSchedule_with_unauthorizedUser() throws Exception {
        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .dayId(1L)
                .title("일정 제목")
                .latitude(37.5642135)
                .longitude(127.0016985)
                .build();

        mockMvc.perform(post("/api/schedules")
                        .content(createJson(request))
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
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        String emptyContent = "";

        mockMvc.perform(post("/api/schedules")
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
    public void createSchedule_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidContent = """
                {
                    "dayId"+ 1,
                    "tripId": 2,
                    "title": 괄호로 감싸지지 않은 문자열,
                    "placeId": "place-5964",
                    "placeName": "장소명",
                    "latitude": 35.1234,
                    "longitude": 123.1212
                }
                """;

        mockMvc.perform(post("/api/schedules")
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
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_invalidType() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidTypeContent = """
                {
                    "dayId": 1,
                    "tripId": "숫자가 아닌 값",
                    "title": "제목",
                    "placeId": "place-5964",
                    "placeName": "장소명",
                    "latitude": "숫자가 아닌 위도값",
                    "longitude": "숫자가 아닌 경도값"
                }
                """;


        mockMvc.perform(post("/api/schedules")
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
    }

}
