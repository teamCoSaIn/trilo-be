package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import com.cosain.trilo.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.DEFAULT_SEQUENCE_GAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("[통합] 여행 기간 수정 API 테스트")
public class TripPeriodUpdateIntegrationTest extends IntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @DisplayName("초기화되지 않은 여행 기간을")
    @Nested
    public class TestUndecidedTrip {

        @DisplayName("startDate, endDate 모두 null로 변경 -> 변화 없음")
        @Test
        public void testUndecidedTripToUndecidedTrip() throws Exception {
            // given
            User user = setupMockNaverUser();
            Trip trip = setupUndecidedTrip(user.getId());
            log.info("trip = {}", trip);
            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(null, null);

            flushAndClear();

            // when
            ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripid}/period", trip.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                    .content(createRequestJson(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON));

            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> days = findTrip.getDays();
            List<Schedule> schedules = findTrip.getTemporaryStorage();

            log.info("findTrip = {}", findTrip);

            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
            assertThat(days).isEmpty();
            assertThat(schedules).isEmpty();
        }

        @DisplayName("startDate, endDate 특정 날짜 지정 -> 기간 초기화 됨")
        @Test
        public void testInitPeriod() throws Exception {
            // given
            User user = setupMockNaverUser();
            Trip trip = setupUndecidedTrip(user.getId());
            log.info("trip = {}", trip);

            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);
            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);

            flushAndClear();

            // when
            ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                    .content(createRequestJson(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON));

            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findSchedules = findTrip.getTemporaryStorage();

            log.info("findTrip = {}", findTrip);

            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(startDate, endDate));
            assertThat(findDays.size()).isEqualTo(2);
            assertThat(findDays).map(Day::getTripDate).containsExactly(startDate, endDate);
            assertThat(findSchedules).isEmpty();
        }

    }

    @DisplayName("기간이 지정되어 있는 여행의 기간을")
    @Nested
    public class TestDecidedTrip {

        @DisplayName("같은 기간으로 변경 -> 변화 없음")
        @Test
        public void testUpdateSamePeriod() throws Exception {
            // given
            User user = setupMockNaverUser();
            LocalDate startDate = LocalDate.of(2023,3,1);
            LocalDate endDate = LocalDate.of(2023,3,2);

            Trip trip = setupDecidedTrip(user.getId(), startDate, endDate);
            log.info("trip = {}", trip);

            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
            flushAndClear();

            // when
            ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripid}/period", trip.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                    .content(createRequestJson(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON));

            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> days = findTrip.getDays();
            List<Schedule> schedules = findTrip.getTemporaryStorage();

            log.info("findTrip = {}", findTrip);

            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());
            assertThat(days).map(Day::getTripDate).containsExactly(startDate, endDate);
            assertThat(schedules).isEmpty();
        }

        @DisplayName("다른 유효한 기간으로 변경 -> 성공")
        @Test
        public void testChangeOtherPeriod() throws Exception {
            // given ===================================================================================================================================================
            User user = setupMockNaverUser();

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 4);

            LocalDate newStartDate = LocalDate.of(2023,3,3);
            LocalDate newEndDate = LocalDate.of(2023,3,5);

            Trip trip = setupDecidedTrip(user.getId(), beforeStartDate, beforeEndDate);
            log.info("trip = {}", trip);
            Day beforeDay1 = trip.getDays().get(0);
            Day beforeDay2 = trip.getDays().get(1);
            Day beforeDay3 = trip.getDays().get(2);
            Day beforeDay4 = trip.getDays().get(3);

            Schedule beforeTempSchedule = setupTemporarySchedule(trip, 1000L);
            Schedule beforeDay1Schedule1 = setupDaySchedule(trip, beforeDay1, 0L);
            Schedule beforeDay1Schedule2 = setupDaySchedule(trip, beforeDay1, 100L);
            Schedule beforeDay2Schedule1 = setupDaySchedule(trip, beforeDay2, 300L);
            Schedule beforeDay2Schedule2 = setupDaySchedule(trip, beforeDay2, 400L);
            Schedule beforeDay3Schedule = setupDaySchedule(trip, beforeDay3, 0L);
            Schedule beforeDay4Schedule = setupDaySchedule(trip, beforeDay4, 100L);

            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
            flushAndClear();

            // when : [03.01 ~ 03.04] -> [03.03 ~ 03.05] =============================================================================================================
            ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                    .content(createRequestJson(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON));
            flushAndClear();

            // then ==================================================================================================================================================
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findTempSchedules = findTrip.getTemporaryStorage();
            Schedule afterDay3Schedule = scheduleRepository.findById(beforeDay3Schedule.getId()).orElseThrow(IllegalStateException::new);
            Schedule afterDay4Schedule = scheduleRepository.findById(beforeDay4Schedule.getId()).orElseThrow(IllegalStateException::new);

            log.info("findTrip = {}", findTrip);

            // then1 : 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            // then2 : 수정된 여행의 상태, 기간 검증
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(newStartDate, newEndDate));

            // then3 : 수정된 기간에 속하지 않는 Day 존재하지 않음 검증
            assertThat(findDayById(beforeDay1.getId())).isNull();
            assertThat(findDayById(beforeDay2.getId())).isNull();

            // then4 : 수정된 후 여행이 가진 Day 검증
            assertThat(findDays.size()).isEqualTo(3);
            assertThat(findDays).map(Day::getTripDate)
                    .containsExactly(LocalDate.of(2023,3,3), LocalDate.of(2023,3,4), LocalDate.of(2023,3,5));

            // then4 : 새로운 기간에 남아있는 Day들의 Schedule이 상태를 그대로 유지하고 있는 지 검증
            assertThat(afterDay3Schedule.getDay().getId()).isEqualTo(beforeDay3.getId());
            assertThat(afterDay3Schedule.getScheduleIndex()).isEqualTo(beforeDay3Schedule.getScheduleIndex());
            assertThat(afterDay4Schedule.getDay().getId()).isEqualTo(beforeDay4.getId());
            assertThat(afterDay4Schedule.getScheduleIndex()).isEqualTo(beforeDay4Schedule.getScheduleIndex());

            // then5 : 여행의 임시보관함 검증
            assertThat(findTempSchedules.size()).isEqualTo(5);
            assertThat(findTempSchedules).map(Schedule::getId)
                    .containsExactly(beforeTempSchedule.getId(), beforeDay1Schedule1.getId(), beforeDay1Schedule2.getId(), beforeDay2Schedule1.getId(), beforeDay2Schedule2.getId());

            // then6: 삭제된 Day에 속한 Schedule 들이 기존 임시보관함 뒤에 순서대로 잘 배치됐는 지 검증
            assertThat(findTempSchedules)
                    .map(sch -> sch.getScheduleIndex().getValue())
                    .containsExactly(0L, DEFAULT_SEQUENCE_GAP, DEFAULT_SEQUENCE_GAP * 2, DEFAULT_SEQUENCE_GAP * 3, DEFAULT_SEQUENCE_GAP * 4);
        }

        @DisplayName("startDate, endDate 모두 null로 변경 -> 예외 발생")
        @Test
        public void testDecidedTripToUndecidedTrip() throws Exception {
            // given
            User user = setupMockNaverUser();

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);

            Trip trip = setupDecidedTrip(user.getId(), beforeStartDate, beforeEndDate);
            log.info("trip = {}", trip);

            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(null, null);
            flushAndClear();

            // when
            ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                    .content(createRequestJson(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON));

            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findSchedules = findTrip.getTemporaryStorage();

            log.info("findTrip = {}", findTrip);

            resultActions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("trip-0006"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());

            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(beforeStartDate, beforeEndDate));
            assertThat(findDays.size()).isEqualTo(2);
            assertThat(findDays).map(Day::getTripDate).containsExactly(beforeStartDate, beforeEndDate);
            assertThat(findSchedules).isEmpty();
        }

    }


    @DisplayName("startDate만 null로 수정 -> 요청 검증 예외 발생")
    @Test
    public void test_StartDate_is_Null() throws Exception {
        // given
        User user = setupMockNaverUser();

        LocalDate newStartDate = null; // 시작일만 null
        LocalDate newEndDate = LocalDate.of(2023, 3, 1);

        Trip trip = setupUndecidedTrip(user.getId());
        log.info("trip = {}", trip);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
        flushAndClear();


        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> days = findTrip.getDays();
        List<Schedule> schedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0005"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(days).isEmpty();
        assertThat(schedules).isEmpty();
    }

    @DisplayName("endDate만 null로 수정 -> 요청 검증 예외 발생")
    @Test
    public void test_EndDate_is_Null() throws Exception {
        // given
        User user = setupMockNaverUser();

        LocalDate newStartDate = LocalDate.of(2023,3,1);
        LocalDate newEndDate = null; // 종료일만 null

        Trip trip = setupUndecidedTrip(user.getId());
        log.info("trip = {}", trip);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
        flushAndClear();


        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> days = findTrip.getDays();
        List<Schedule> schedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0005"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(days).isEmpty();
        assertThat(schedules).isEmpty();
    }


    @DisplayName("startDate, endDate 전후 관계 모순되게 수정 -> 요청 검증 예외 발생")
    @Test
    public void test_StartDate_is_After_EndDate() throws Exception {
        // given
        User user = setupMockNaverUser();

        LocalDate newStartDate = LocalDate.of(2023, 3, 2); // 전후 관계 모순
        LocalDate newEndDate = LocalDate.of(2023, 3, 1);

        Trip trip = setupUndecidedTrip(user.getId());
        log.info("trip = {}", trip);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
        flushAndClear();


        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> days = findTrip.getDays();
        List<Schedule> schedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0005"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(days).isEmpty();
        assertThat(schedules).isEmpty();
    }

    @DisplayName("10일을 넘는 기간으로 변경하려 시도 -> 요청 검증 예외 발생")
    @Test
    public void testLongPeriod() throws Exception {
        // given
        User user = setupMockNaverUser();

        LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
        LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);
        Trip trip = setupDecidedTrip(user.getId(), beforeStartDate, beforeEndDate);
        log.info("trip = {}", trip);

        LocalDate newStartDate = LocalDate.of(2023, 3, 1);
        LocalDate newEndDate = LocalDate.of(2023, 3, 11);
        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);

        flushAndClear();

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();
        List<Schedule> findSchedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("trip-0009"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(beforeStartDate, beforeEndDate));
        assertThat(findDays.size()).isEqualTo(2);
        assertThat(findDays).map(Day::getTripDate).containsExactly(beforeStartDate, beforeEndDate);
        assertThat(findSchedules).isEmpty();
    }

    @DisplayName("존재하지 않는 여행 수정 시도 -> 예외 발생")
    @Test
    public void testTripNotFound() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        Long otherTripId = trip.getId() + 1L;

        log.info("trip = {}", trip);
        flushAndClear();

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(LocalDate.of(2023,3,1), LocalDate.of(2023,3,2));

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", otherTripId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();
        List<Schedule> findSchedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("trip-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(findDays).isEmpty();
        assertThat(findSchedules).isEmpty();
    }

    @DisplayName("권한이 없는 사람이 여행 수정 시도 -> 예외 발생")
    @Test
    public void testNoUpdateAuthority() throws Exception {
        // given
        User tripOwner = setupMockNaverUser();
        User otherUser = setupMockGoogleUser();

        LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
        LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);
        Trip trip = setupDecidedTrip(tripOwner.getId(), beforeStartDate, beforeEndDate);
        log.info("trip = {}", trip);

        LocalDate newStartDate = LocalDate.of(2023, 3, 4);
        LocalDate newEndDate = LocalDate.of(2023, 3, 5);
        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
        flushAndClear();

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/period", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(otherUser))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();
        List<Schedule> findSchedules = findTrip.getTemporaryStorage();

        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("trip-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(beforeStartDate, beforeEndDate));
        assertThat(findDays.size()).isEqualTo(2);
        assertThat(findDays).map(Day::getTripDate).containsExactly(beforeStartDate, beforeEndDate);
        assertThat(findSchedules).isEmpty();
    }

    private Day findDayById(Long dayId) {
        return em.find(Day.class, dayId);
    }

}
