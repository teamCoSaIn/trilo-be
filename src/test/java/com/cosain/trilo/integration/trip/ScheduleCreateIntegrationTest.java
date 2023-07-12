package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.*;
import com.cosain.trilo.trip.presentation.schedule.dto.request.RequestCoordinate;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleCreateRequest;
import com.cosain.trilo.trip.presentation.schedule.dto.response.ScheduleCreateResponse;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
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
    @WithMockUser
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
    @WithMockUser
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
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].errorCode", hasItem("place-0001")))
                .andExpect(jsonPath("$.errors[*].errorCode", hasItem("place-0002")))
                .andExpect(jsonPath("$.errors[*].errorMessage").exists())
                .andExpect(jsonPath("$.errors[*].errorDetail").exists());
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
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @DisplayName("일정 제목이 null 아니고 20자 이하(공백 허용) -> 정상 생성")
    @ValueSource(strings = {"일정 제목", "", "     "})
    @ParameterizedTest
    public void tripIdNullTest(String rawScheduleTitle) throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Schedule beforeSchedule = setupTemporarySchedule(trip, 0L);

        var request = defaultRequestBuilder(null, trip.getId()).title(rawScheduleTitle).build();

        // when : 일정 제목 정상 케이스
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then ==============================================================================================

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").isNotEmpty());

        // then 2: 응답 데이터 검증
        var response = createResponseObject(resultActions, ScheduleCreateResponse.class);
        Schedule findSchedule = scheduleRepository.findById(response.getScheduleId()).orElseThrow(IllegalStateException::new);

        assertThat(findSchedule.getTrip().getId()).isEqualTo(trip.getId());
        assertThat(findSchedule.getDay()).isEqualTo(null);
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(ScheduleTitle.of(request.getTitle()));
        assertThat(findSchedule.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
        assertThat(findSchedule.getScheduleIndex()).isEqualTo(beforeSchedule.getScheduleIndex().generateBeforeIndex());
        assertThat(findSchedule.getPlace())
                .isEqualTo(Place.of(request.getPlaceId(), request.getPlaceName(), Coordinate.of(request.getCoordinate().getLatitude(), request.getCoordinate().getLongitude())));
        assertThat(findSchedule.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
    }

    @Test
    @DisplayName("일정 제목 null -> 입력 검증 실패 400 예외")
    public void nullTitle() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId()).title(null).build();
        flushAndClear();

        // when : null 제목
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("schedule-0008"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }
    @Test
    @DisplayName("20자를 넘는 일정 제목 -> 입력 검증 실패 400 예외")
    public void tooLongScheduleTitle() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        String tooLongTitle = "가".repeat(21);
        var request = defaultRequestBuilder(null, trip.getId()).title(tooLongTitle).build();
        flushAndClear();

        // when : 20자를 넘는 제목
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("schedule-0008"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("위도 누락 -> 입력 검증 실패 400 예외")
    public void nullLatitude() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(null, 34.122))
                .build();

        flushAndClear();

        // when : 위도 누락
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("경도 누락 -> 입력 검증 실패 400 예외")
    public void nullLongitude() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(34.2121, null))
                .build();

        flushAndClear();

        // when : 경도 누락
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("위도, 경도 누락 -> 입력 검증 실패 400 예외")
    public void nullLatitudeAndLongitude() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(null, null))
                .build();

        flushAndClear();

        // when : 위도, 경도 모두 누락
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("위도가 제한보다 큰 값 -> 입력 검증 실패 400 예외")
    public void bigLatitudeTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        double latitude = Coordinate.MAX_LATITUDE + 0.001;
        double longitude = 71.126;

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(latitude, longitude))
                .build();

        flushAndClear();

        // when : 위도가 제한보다 큼
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("위도가 제한보다 작은 값 -> 입력 검증 실패 400 예외")
    public void smallLatitudeTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        double latitude = Coordinate.MIN_LATITUDE - 0.001;
        double longitude = 71.126;

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(latitude, longitude))
                .build();

        flushAndClear();

        // when : 위도가 제한보다 작음
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("경도가 제한보다 큰 값 -> 입력 검증 실패 400 예외")
    public void bigLongitudeTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        double latitude = 34.212;
        double longitude = Coordinate.MAX_LONGITUDE + 0.001;

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(latitude, longitude))
                .build();

        flushAndClear();

        // when : 경도가 제한보다 큼
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @Test
    @DisplayName("경도가 제한보다 작은 값 -> 입력 검증 실패 400 예외")
    public void smallLongitudeTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        double latitude = 34.212;
        double longitude = Coordinate.MIN_LONGITUDE - 0.001;

        var request = defaultRequestBuilder(null, trip.getId())
                .coordinate(new RequestCoordinate(latitude, longitude))
                .build();

        flushAndClear();

        // when : 경도가 제한보다 작음
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
                .andExpect(jsonPath("$.errors[0].errorCode").value("place-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());
    }

    @DisplayName("여행을 찾을 수 없음 -> 예외 발생")
    @Test
    void tripNotFound() throws Exception {
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Long notExistTripId = trip.getId() + 1L;

        var request = defaultRequestBuilder(null, notExistTripId).build();
        flushAndClear();

        // when : 존재하지 않는 여행 Id
        ResultActions resultActions = mockMvc.perform(post("/api/schedules")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("trip-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
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


    private Trip setupUndecidedTrip(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    private Trip setupDecidedTrip(Long tripperId, LocalDate startDate, LocalDate endDate) {
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);
        return trip;
    }

    private Schedule setupTemporarySchedule(Trip trip, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

    private Schedule setupDaySchedule(Trip trip, Day day, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.day_NullId(trip, day, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

    private ScheduleCreateRequest.ScheduleCreateRequestBuilder defaultRequestBuilder(Long dayId, Long tripId) {
        return ScheduleCreateRequest.builder()
                .dayId(dayId)
                .tripId(tripId)
                .title("일정 제목")
                .placeId("place-id")
                .placeName("place-name")
                .coordinate(new RequestCoordinate(37.564213, 127.001698));
    }

}
