package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.*;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleCreateResponse;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] 일정 생성 API 테스트")
class ScheduleCreateIntegrationTest extends IntegrationTest {

    @Autowired
    TripRepository tripRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("임시보관함에 일정 생성 -> 맨 앞에 일정 생성됨")
    public void testTempScheduleCreate() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Schedule beforeSchedule = setupTemporarySchedule(trip, 0L);

        var request = defaultRequestBuilder(null, trip.getId()).build();
        flushAndClear();

        // when : 임시보관함에 일정 생성 요청
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
        flushAndClear();

        // then =====================================================================================================================================

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").isNotEmpty());

        // then 2: 생성된 일정의 필드 검증
        var response = createResponseObject(resultActions, ScheduleCreateResponse.class);
        Schedule findSchedule = scheduleRepository.findById(response.getScheduleId()).orElseThrow(IllegalStateException::new);

        assertThat(findSchedule.getTrip().getId()).isEqualTo(trip.getId());
        assertThat(findSchedule.getDay()).isNull();
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(ScheduleTitle.of(request.getTitle()));
        assertThat(findSchedule.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
        assertThat(findSchedule.getScheduleIndex()).isEqualTo(beforeSchedule.getScheduleIndex().generateBeforeIndex());
        assertThat(findSchedule.getPlace())
                .isEqualTo(Place.of(request.getPlaceId(), request.getPlaceName(), Coordinate.of(request.getCoordinate().getLatitude(), request.getCoordinate().getLongitude())));
        assertThat(findSchedule.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
    }

    @Test
    @DisplayName("Day에 일정 생성 -> 맨 뒤에 일정 생성됨")
    public void testDayScheduleCreate() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupDecidedTrip(user.getId() , LocalDate.of(2023,3,1), LocalDate.of(2023,3,1));
        Day day = trip.getDays().get(0);
        Schedule beforeSchedule1 = setupDaySchedule(trip, day, 0L);
        Schedule beforeSchedule2 = setupDaySchedule(trip, day, 100L);
        Schedule beforeSchedule3 = setupDaySchedule(trip, day, 200L);

        var request = defaultRequestBuilder(day.getId(), trip.getId()).build();
        flushAndClear();

        // when : Day에 일정 생성 요청
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
        flushAndClear();

        // then =====================================================================================================================================

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").isNotEmpty());

        // then 2: 생성된 일정의 필드 검증
        var response = createResponseObject(resultActions, ScheduleCreateResponse.class);
        Schedule findSchedule = scheduleRepository.findById(response.getScheduleId()).orElseThrow(IllegalStateException::new);

        assertThat(findSchedule.getTrip().getId()).isEqualTo(trip.getId());
        assertThat(findSchedule.getDay().getId()).isEqualTo(day.getId());
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(ScheduleTitle.of(request.getTitle()));
        assertThat(findSchedule.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
        assertThat(findSchedule.getScheduleIndex()).isEqualTo(beforeSchedule3.getScheduleIndex().generateNextIndex());
        assertThat(findSchedule.getPlace())
                .isEqualTo(Place.of(request.getPlaceId(), request.getPlaceName(), Coordinate.of(request.getCoordinate().getLatitude(), request.getCoordinate().getLongitude())));
        assertThat(findSchedule.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
    }


    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void createSchedule_with_unauthorizedUser() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        flushAndClear();
        var request = defaultRequestBuilder(null, trip.getId()).build();

        // when : 미인증 사용자의 일정 생성 요청
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                // 인증헤더 누락
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
        flushAndClear();

        // then
        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
        flushAndClear();

        // then2: 여행의 임시보관함에 일정이 없음을 검증
        List<Schedule> findSchedules = tripRepository
                .findById(trip.getId())
                .orElseThrow(IllegalStateException::new)
                .getTemporaryStorage();

        assertThat(findSchedules).isEmpty();
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_emptyContent() throws Exception {
        // given
        User user = setupMockNaverUser();
        String emptyContent = "";
        flushAndClear();

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(emptyContent)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_invalidContent() throws Exception {
        // given
        User user = setupMockNaverUser();
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
        flushAndClear();

        // when : 일정 생성 요청
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(invalidContent)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
        flushAndClear();

        // then : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createSchedule_with_invalidType() throws Exception {
        // given
        User user = setupMockNaverUser();
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
        flushAndClear();

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(invalidTypeContent)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
        flushAndClear();

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("좌표 누락 데이터 -> 입력 검증 실패 400 예외")
    public void createSchedule_with_nullCoordinate() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId()).coordinate(null).build();
        flushAndClear();

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0002"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists())
                .andExpect(jsonPath("$.errors[0].field").exists());
    }

    @Test
    @DisplayName("tripId null -> 입력 검증 실패 400 예외")
    public void tripIdNullTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, null).build();

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].errorCode").value("schedule-0007"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists())
                .andExpect(jsonPath("$.errors[0].field").exists());
    }

    @DisplayName("다른 사용자가 일정 생성 시도 -> 예외 발생")
    @Test
    void tripNoAuthorityTripper() throws Exception {
        User user = setupMockNaverUser();
        User otherUser = setupMockGoogleUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId()).build();
        flushAndClear();

        // when : 다른 사용자의 요청
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(otherUser))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("schedule-0002"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    private ScheduleCreateRequest.ScheduleCreateRequestBuilder defaultRequestBuilder(Long dayId, Long tripId) {
        return ScheduleCreateRequest.builder()
                .dayId(dayId)
                .tripId(tripId)
                .title("일정 제목")
                .placeId("place-id")
                .placeName("place-name")
                .coordinate(new ScheduleCreateRequest.CoordinateDto(37.564213, 127.001698));
    }

}
