package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
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

/**
 * 여행 기간수정 기능에 대한 통합 테스트 클래스입니다.
 */
@Slf4j
@DisplayName("[통합] 여행 기간 수정 API 테스트")
public class TripPeriodUpdateIntegrationTest extends IntegrationTest {

    /**
     * 테스트에서 여행의 기간수정 여부를 확인하기 위한 리포지토리 의존성
     */
    @Autowired
    private TripRepository tripRepository;

    /**
     * 테스트에서 일정들의 임시보관함 이동 여부를 확인하기 위한 리포지토리 의존성
     */
    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 초기화되지 않은 여행들의 기간을 변경했을 때에 대한 테스트들입니다.
     */
    @DisplayName("초기화되지 않은 여행 기간을")
    @Nested
    public class TestUndecidedTrip {

        /**
         * 초기화되지 않은 여행의 시작일, 종료일을 모두 null로 변경 -> 변화 없음
         * <ul>
         *     <li>기간수정이 성공했다는 응답이 와야합니다. (200 Ok)</li>
         *     <li>여행의 상태, 기간은 그대로여야 합니다.</li>
         * </ul>
         */
        @DisplayName("startDate, endDate 모두 null로 변경 -> 변화 없음")
        @Test
        public void testUndecidedTripToUndecidedTrip() throws Exception {
            // given
            User requestUser = setupMockNaverUser();
            Trip trip = setupUndecidedTrip(requestUser.getId());
            var request = new TripPeriodUpdateRequest(null, null);
            flushAndClear();

            // when
            ResultActions resultActions = runTest(trip.getId(), createRequestJson(request), requestUser);
            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> days = findTrip.getDays();
            List<Schedule> schedules = findTrip.getTemporaryStorage();

            // 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            // 여행의 필드 검증
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());

            // Day 생성 안 됨
            assertThat(days).isEmpty();

            // 일정 없음
            assertThat(schedules).isEmpty();
        }

        /**
         * 초기화되지 않은 여행의 시작일, 종료일을 모두 특정 날짜로 수정한 경우에 대한 테스트입니다.
         * <ul>
         *     <li>기간수정이 성공했다는 응답이 와야합니다. (200 Ok)</li>
         *     <li>여행의 상태, 기간이 변경됩니다.</li>
         *     <li>Day들이 생성됩니다.</li>
         * </ul>
         */
        @DisplayName("startDate, endDate 특정 날짜 지정 -> 기간 초기화 됨")
        @Test
        public void testInitPeriod() throws Exception {
            // given
            User requestUser = setupMockNaverUser();
            Trip trip = setupUndecidedTrip(requestUser.getId());

            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);
            TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
            flushAndClear();

            // when
            ResultActions resultActions = runTest(trip.getId(), createRequestJson(request), requestUser);
            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findSchedules = findTrip.getTemporaryStorage();

            // 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            // 여행 상태, 기간 검증
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(startDate, endDate));

            // Day 검증
            assertThat(findDays.size()).isEqualTo(2);
            assertThat(findDays).map(Day::getTripDate).containsExactly(startDate, endDate);

            // 일정 없음
            assertThat(findSchedules).isEmpty();
        }

    }

    /**
     * 여행의 기간이 정해진 상태에서, 여행의 기간을 수정할 때에 대한 테스트들입니다.
     */
    @DisplayName("기간이 지정되어 있는 여행의 기간을")
    @Nested
    public class TestDecidedTrip {

        /**
         * 초기화 된 여행의 시작일, 종료일을 모두 같은 날짜로 변경 -> 변화 없음
         * <ul>
         *     <li>기간수정이 성공했다는 응답이 와야합니다. (200 Ok)</li>
         *     <li>여행의 상태, 기간은 그대로여야 합니다.</li>
         * </ul>
         */
        @DisplayName("같은 기간으로 변경 -> 변화 없음")
        @Test
        public void testUpdateSamePeriod() throws Exception {
            // given
            User user = setupMockNaverUser();
            LocalDate startDate = LocalDate.of(2023,3,1);
            LocalDate endDate = LocalDate.of(2023,3,2);

            Trip trip = setupDecidedTrip(user.getId(), startDate, endDate);
            var request = new TripPeriodUpdateRequest(startDate, endDate);
            flushAndClear();

            // when
            ResultActions resultActions = runTest(trip.getId(), createRequestJson(request), user); // 같은 기간으로 수
            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> days = findTrip.getDays();
            List<Schedule> schedules = findTrip.getTemporaryStorage();

            // 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            // 여행의 상태는 그대로
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());

            // Day들 또한 상태 유지
            assertThat(days).map(Day::getTripDate).containsExactly(startDate, endDate);

            // 일정 없음
            assertThat(schedules).isEmpty();
        }

        /**
         * 초기화 된 여행의 시작일, 종료일을 일부 겹치고, 일부는 겹치지 않는 기간으로 변경 -> 변화
         * <ul>
         *     <li>기간수정이 성공했다는 응답이 와야합니다. (200 Ok)</li>
         *     <li>여행의 상태, 기간이 변경되어야 합니다.</li>
         *     <li>기존 기간에 속하지만 새로운 기간에 속하지 않는 Day들은 삭제됩니다</li>
         *     <li>기존 기간에 속하지만 새로운 기간에 속하지 않는 Day들의 일정들이 임시보관함에 이동</li>
         *     <li>기존 기간에 속하지않는 새로운 기간의 Day들이 생성됩니다</li>
         * </ul>
         */
        @DisplayName("다른 유효한 기간으로 변경 -> 성공")
        @Test
        public void testChangeOtherPeriod() throws Exception {
            // given
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

            var request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
            flushAndClear();

            // when : [03.01 ~ 03.04] -> [03.03 ~ 03.05] =============================================================================================================
            ResultActions resultActions = runTest(trip.getId(), createRequestJson(request), user);
            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findTempSchedules = findTrip.getTemporaryStorage();
            Schedule afterDay3Schedule = scheduleRepository.findById(beforeDay3Schedule.getId()).orElseThrow(IllegalStateException::new);
            Schedule afterDay4Schedule = scheduleRepository.findById(beforeDay4Schedule.getId()).orElseThrow(IllegalStateException::new);

            log.info("findTrip = {}", findTrip);

            // 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(trip.getId()));

            // 수정된 여행의 상태, 기간 검증
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(newStartDate, newEndDate));

            // 수정된 기간에 속하지 않는 Day 존재하지 않음 검증
            assertThat(findDayById(beforeDay1.getId())).isNull();
            assertThat(findDayById(beforeDay2.getId())).isNull();

            // 수정된 후 여행이 가진 Day 검증
            assertThat(findDays.size()).isEqualTo(3);
            assertThat(findDays).map(Day::getTripDate)
                    .containsExactly(LocalDate.of(2023,3,3), LocalDate.of(2023,3,4), LocalDate.of(2023,3,5));

            // 새로운 기간에 남아있는 Day들의 Schedule이 상태를 그대로 유지하고 있는 지 검증
            assertThat(afterDay3Schedule.getDay().getId()).isEqualTo(beforeDay3.getId());
            assertThat(afterDay3Schedule.getScheduleIndex()).isEqualTo(beforeDay3Schedule.getScheduleIndex());
            assertThat(afterDay4Schedule.getDay().getId()).isEqualTo(beforeDay4.getId());
            assertThat(afterDay4Schedule.getScheduleIndex()).isEqualTo(beforeDay4Schedule.getScheduleIndex());

            // 여행의 임시보관함 검증
            assertThat(findTempSchedules.size()).isEqualTo(5);
            assertThat(findTempSchedules).map(Schedule::getId)
                    .containsExactly(beforeTempSchedule.getId(), beforeDay1Schedule1.getId(), beforeDay1Schedule2.getId(), beforeDay2Schedule1.getId(), beforeDay2Schedule2.getId());

            // 삭제된 Day에 속한 Schedule 들이 기존 임시보관함 뒤에 순서대로 잘 배치됐는 지 검증
            assertThat(findTempSchedules)
                    .map(sch -> sch.getScheduleIndex().getValue())
                    .containsExactly(0L, DEFAULT_SEQUENCE_GAP, DEFAULT_SEQUENCE_GAP * 2, DEFAULT_SEQUENCE_GAP * 3, DEFAULT_SEQUENCE_GAP * 4);
        }

        /**
         * 기간이 정해져있는 상태에서, 기간의 시작일/종료일을 모두 null 로 변경 시도할 때 예외 발생함을 검증
         */
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
            var resultActions = runTest(trip.getId(), createRequestJson(request), user);
            flushAndClear();

            // then
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
            List<Day> findDays = findTrip.getDays();
            List<Schedule> findSchedules = findTrip.getTemporaryStorage();

            // 응답 메시지 검증
            resultActions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("trip-0006"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());

            // 여행의 상태, 기간 변경 없음
            assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
            assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(beforeStartDate, beforeEndDate));

            // 여행이 가진 Day 변경 없음
            assertThat(findDays.size()).isEqualTo(2);
            assertThat(findDays).map(Day::getTripDate).containsExactly(beforeStartDate, beforeEndDate);

            // 일정 없음
            assertThat(findSchedules).isEmpty();
        }

    }

    /**
     * 존재하지 않는 여행 식별자를 전달시 예외 발생함을 검증합니다.
     */
    @DisplayName("존재하지 않는 여행 수정 시도 -> 예외 발생")
    @Test
    public void testTripNotFound() throws Exception {
        // given
        User user = setupMockNaverUser();
        Trip trip = setupUndecidedTrip(user.getId());

        Long otherTripId = trip.getId() + 1L;

        log.info("trip = {}", trip);
        flushAndClear();

        var request = new TripPeriodUpdateRequest(LocalDate.of(2023,3,1), LocalDate.of(2023,3,2));

        // when
        var resultActions = runTest(otherTripId, createRequestJson(request), user);
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();
        List<Schedule> findSchedules = findTrip.getTemporaryStorage();

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("trip-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 여행의 상태, 기간은 그대로임
        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.UNDECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());

        // Day 생성 안 됨
        assertThat(findDays).isEmpty();

        // 일정 없음
        assertThat(findSchedules).isEmpty();
    }

    /**
     * 권한이 없는 사람이 여행 기간 수정 요청 시, 예외가 발생함을 검증합니다.
     */
    @DisplayName("권한이 없는 사람이 여행 수정 시도 -> 예외 발생")
    @Test
    public void testNoUpdateAuthority() throws Exception {
        // given
        User tripOwner = setupMockNaverUser();
        User otherUser = setupMockGoogleUser();

        LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
        LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);
        Trip trip = setupDecidedTrip(tripOwner.getId(), beforeStartDate, beforeEndDate);

        LocalDate newStartDate = LocalDate.of(2023, 3, 4);
        LocalDate newEndDate = LocalDate.of(2023, 3, 5);
        var request = new TripPeriodUpdateRequest(newStartDate, newEndDate);
        flushAndClear();

        // when
        ResultActions resultActions = runTest(trip.getId(), createRequestJson(request), otherUser);
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();
        List<Schedule> findSchedules = findTrip.getTemporaryStorage();

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("trip-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 여행 기간, 상태 변경 없음
        assertThat(findTrip.getStatus()).isEqualTo(TripStatus.DECIDED);
        assertThat(findTrip.getTripPeriod()).isEqualTo(TripPeriod.of(beforeStartDate, beforeEndDate));

        // Day 상태 변경 없음
        assertThat(findDays.size()).isEqualTo(2);
        assertThat(findDays).map(Day::getTripDate).containsExactly(beforeStartDate, beforeEndDate);

        // 일정 없음
        assertThat(findSchedules).isEmpty();
    }

    /**
     * 일치하는 식별자의 Day를 찾습니다.
     * @param dayId Day의 식별자
     * @return Day
     */
    private Day findDayById(Long dayId) {
        return em.find(Day.class, dayId);
    }


    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId 여행의 식별자
     * @param content : 요청 본문(body)
     * @param requestUser : 요청 사용자
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId, String content, User requestUser) throws Exception {
        return mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
