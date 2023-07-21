package com.cosain.trilo.unit.trip.presentation.schedule;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleCreateController;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("일정 생성 API 테스트")
@WebMvcTest(ScheduleCreateController.class)
class ScheduleCreateControllerTest extends RestControllerTest {

    @MockBean
    private ScheduleCreateService scheduleCreateService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 올바른 요청 -> 일정 생성됨")
    public void createSchedule_with_authorizedUser() throws Exception {

        Long tripId = 1L;
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);
        Long dayId = 3L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";
        Double latitude = 37.564213;
        Double longitude = 127.001698;

        String requestJson = String.format("""
                {
                    "dayId": %d,
                    "tripId": %d,
                    "title": "%s",
                    "placeId": "%s",
                    "placeName": "%s",
                    "coordinate": {
                        "latitude": %f,
                        "longitude": %f
                    }
                }
                """, dayId, tripId, rawScheduleTitle, placeId, placeName, latitude, longitude);

        var command = ScheduleCreateCommand.of(requestTripperId, tripId, dayId, rawScheduleTitle, placeId, placeName, latitude, longitude);
        Long createdScheduleId = 3L;

        given(scheduleCreateService.createSchedule(eq(command))).willReturn(createdScheduleId);

        mockMvc.perform(post("/api/schedules")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(requestJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").value(createdScheduleId));

        verify(scheduleCreateService, times(1)).createSchedule(eq(command));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void createSchedule_with_unauthorizedUser() throws Exception {
        Long tripId = 1L;
        Long dayId = 2L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";
        Double latitude = 37.5642;
        Double longitude = 127.0016;

        String requestJson = String.format("""
                {
                    "dayId": %d,
                    "tripId": %d,
                    "title": "%s",
                    "placeId": "%s",
                    "placeName": "%s",
                    "coordinate": {
                        "latitude": %f,
                        "longitude": %f
                    }
                }
                """, dayId, tripId, rawScheduleTitle, placeId, placeName, latitude, longitude);


        mockMvc.perform(post("/api/schedules")
                        .content(requestJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_emptyContent() throws Exception {
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);

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

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_invalidContent() throws Exception {
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);
        String invalidContent = """
                {
                    "dayId"+ 1,
                    "tripId": 2,
                    "title": 괄호로 감싸지지 않은 문자열,
                    "placeId": "place-5964",
                    "placeName": "장소명",
                    "coordinate": {
                        "latitude": 34.124,
                        "longitude": 123.124
                    }
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

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_invalidType() throws Exception {
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);
        String invalidTypeContent = """
                {
                    "dayId": 1,
                    "tripId": 7,
                    "title": "제목",
                    "placeId": "place-5964",
                    "placeName": "장소명",
                    "coordinate": {
                        "latitude": "숫자가 아닌 위도값",
                        "longitude": "숫자가 아닌 경도값"
                    }
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

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("좌표 누락 데이터 -> 입력 검증 실패 400 예외")
    public void createSchedule_with_nullCoordinate() throws Exception {
        mockingForLoginUserAnnotation();

        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";

        String requestJson = String.format("""
                {
                    "dayId": %d,
                    "tripId": %d,
                    "title": "%s",
                    "placeId": "%s",
                    "placeName": "%s"
                }
                """, dayId, tripId, rawScheduleTitle, placeId, placeName); // 좌표 누락됨

        mockMvc.perform(post("/api/schedules")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(requestJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("tripId 누락 데이터 -> 입력 검증 실패 400 예외")
    public void createSchedule_with_nullTripId() throws Exception {
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long dayId = 2L;
        Long tripId = null;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";
        Double latitude = 37.5642;
        Double longitude = 127.0016;

        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .tripId(tripId)
                .dayId(dayId)
                .title(rawScheduleTitle)
                .placeId(placeId)
                .placeName(placeName)
                .coordinate(new ScheduleCreateRequest.CoordinateDto(latitude, longitude))
                .build();

        mockMvc.perform(post("/api/schedules")
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
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].errorCode").value("schedule-0007"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

    @Test
    @DisplayName("tripId, 좌표 누락 데이터 -> 입력 검증 실패 400 예외")
    public void createSchedule_with_nullTripId_and_nullCoordinate() throws Exception {
        long tripperId = 1L;
        mockingForLoginUserAnnotation(tripperId);

        Long dayId = 2L;
        Long tripId = null;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";

        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .tripId(tripId)
                .dayId(dayId)
                .title(rawScheduleTitle)
                .placeId(placeId)
                .placeName(placeName)
                .coordinate(null)
                .build();

        mockMvc.perform(post("/api/schedules")
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
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].errorCode", hasItem("schedule-0007")))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists())
                .andExpect(jsonPath("$.errors[0].field").exists())
                .andExpect(jsonPath("$.errors[*].errorCode", hasItem("place-0002")))
                .andExpect(jsonPath("$.errors[1].errorMessage").exists())
                .andExpect(jsonPath("$.errors[1].errorDetail").exists())
                .andExpect(jsonPath("$.errors[1].field").exists());

        verify(scheduleCreateService, times(0)).createSchedule(any(ScheduleCreateCommand.class));
    }

}
