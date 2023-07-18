package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 여행 삭제 API 테스트")
class TripDeleteIntegrationTest extends IntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    private User user;
    private Trip trip;
    private Day day1, day2, day3;
    private Schedule tempSchedule, day1Schedule, day2Schedule, day3Schedule;

    @BeforeEach
    void setUp() {
        // common given : user는 [03.01 ~ 03.03] 여행을 계획하고, Day 및 임시보관함에는 각각 일정이 존재함
        user = setupMockGoogleUser();

        trip = setupDecidedTrip(user.getId(), LocalDate.of(2023,3,1), LocalDate.of(2023,3,3));
        day1 = trip.getDays().get(0);
        day2 = trip.getDays().get(1);
        day3 = trip.getDays().get(2);

        tempSchedule = setupTemporarySchedule(trip, 0L);
        day1Schedule = setupDaySchedule(trip, day1, 0L);
        day2Schedule = setupDaySchedule(trip, day2, 0L);
        day3Schedule = setupDaySchedule(trip, day3, 0L);
        flushAndClear();
    }

    @Test
    @DisplayName("인증된 사용자의 올바른 여행 삭제 요청 -> 성공")
    public void deleteTripSuccessTest() throws Exception {
        // given : setup

        // when : 인증된 사용자의 올바른 여행 삭제 요청
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/{tripId}", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then

        // then1 : 응답 메시지 검
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        // then2 : 여행이 존재하지 않음 검증
        assertThat(tripRepository.findById(trip.getId())).isEmpty();

        // then3: 해당 여행의 Day가 존재하지 않음
        assertThat(findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId()))).isEmpty();

        // then4: 해당 여행의 Schedule이 존재하지 않음
        assertThat(findAllScheduleByIds(
                List.of(tempSchedule.getId(), day1Schedule.getId(), day2Schedule.getId(), day3Schedule.getId()))).isEmpty();
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void deleteTrip_with_unauthorizedUser() throws Exception {
        // given : setup

        // when : 미인증 사용자의 요청
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/{tripId}", trip.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // then 1: 응답 메시지 검증(예외)
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 여행은 여전히 존재함
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // then 3: Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // then 4: Schedule 들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
    }

    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void deleteTrip_with_stringTripId() throws Exception {
        // given : setup

        // when : tripId 자리에 숫자가 아닌 문자열이 주입됨
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/가가가")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // then1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 여행은 여전히 존재함
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // then 3: Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // then 4: Schedule 들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
    }

    @Test
    @DisplayName("존재하지 않는 여행 삭제 요청 -> 예외 발생")
    public void tripNotFound() throws Exception {
        // given : setup + notExistTripId
        Long notExistTripId = trip.getId() + 1L;

        // when : 존재하지 않는 여행 삭제 요
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/{tripId}", notExistTripId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("trip-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("다른 사람이 여행 삭제 요청 -> 예외 발생")
    public void noAuthorityTripper() throws Exception {
        // given : setup + otherUser
        User otherUser = setupMockKakaoUser();

        // when : 다른 사람이 삭제 요청
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/{tripId}", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(otherUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // then1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("trip-0007"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 여행은 여전히 존재함
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // then 3: Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // then 4: Schedule 들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
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

    private List<Day> findAllDayByIds(List<Long> dayIds) {
        return em.createQuery("""
                                SELECT d
                                FROM Day as d
                                WHERE d in :dayIds
                                """, Day.class)
                .setParameter("dayIds", dayIds)
                .getResultList();
    }

    private List<Schedule> findAllScheduleByIds(List<Long> scheduleIds) {
        return em.createQuery("""
                        SELECT s
                        FROM Schedule s
                        WHERE s.id in :scheduleIds
                        """, Schedule.class)
                .setParameter("scheduleIds", scheduleIds)
                .getResultList();
    }
}
