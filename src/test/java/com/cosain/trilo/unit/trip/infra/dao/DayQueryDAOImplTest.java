package com.cosain.trilo.unit.trip.infra.dao;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.application.day.service.day_search.DayScheduleDetail;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.dao.DayQueryDAOImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DayQueryDAOImpl 테스트")
public class DayQueryDAOImplTest extends RepositoryTest {

    @Autowired
    private DayQueryDAOImpl dayQueryDAOImpl;

    @Test
    @DisplayName("findDayWithSchedulesByDayId -> Day 및 소속 Schedule 요약정보 목록(순서 오름차순) 함께 조회됨")
    void testFindDayWithSchedulesByDayId() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 2);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);

        Schedule schedule1 = setupDaySchedule(trip, day1, 10000L);
        Schedule schedule2 = setupDaySchedule(trip, day1, 20000L);
        Schedule schedule3 = setupDaySchedule(trip, day1, 30000L);
        Schedule schedule4 = setupDaySchedule(trip, day2, 10000L);

        em.flush();
        em.clear();

        // when
        long findDayId = day1.getId();
        DayScheduleDetail dayScheduleDetail = dayQueryDAOImpl.findDayWithSchedulesByDayId(findDayId).get();

        // then
        int findSchedulesSize = 3;
        assertThat(dayScheduleDetail.getDayId()).isEqualTo(day1.getId());
        assertThat(dayScheduleDetail.getDate()).isEqualTo(day1.getTripDate());
        assertThat(dayScheduleDetail.getTripId()).isEqualTo(trip.getId());
        assertThat(dayScheduleDetail.getSchedules().size()).isEqualTo(findSchedulesSize);
        assertThat(dayScheduleDetail.getSchedules().get(0).getScheduleId()).isEqualTo(schedule1.getId());
        assertThat(dayScheduleDetail.getSchedules().get(2).getScheduleId()).isEqualTo(schedule3.getId());
    }

    @Nested
    @DisplayName("Trip에 속한 Day 목록 조회 시")
    class FindDayScheduleListByTripIdTest {
        @Test
        @DirtiesContext
        @DisplayName("tripId를 통해 Trip 에 매핑된 Day 들과 해당 Day 와 매핑된 Schedule 들이 조회되며 DTO 로 반환된다.")
        void findTest() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 5, 10);
            LocalDate endDate = LocalDate.of(2023, 5, 11);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            Schedule schedule1 = setupDaySchedule(trip, day1, 10000L);
            Schedule schedule2 = setupDaySchedule(trip, day1, 20000L);
            Schedule schedule3 = setupDaySchedule(trip, day1, 30000L);
            Schedule schedule4 = setupDaySchedule(trip, day2, 10000L);
            em.flush();
            em.clear();

            // when
            List<DayScheduleDetail> dayScheduleDetails = dayQueryDAOImpl.findDayScheduleListByTripId(trip.getId());

            // then
            DayScheduleDetail dayScheduleDetail1 = dayScheduleDetails.get(0);
            DayScheduleDetail dayScheduleDetail2 = dayScheduleDetails.get(1);
            List<ScheduleSummary> schedules1 = dayScheduleDetail1.getSchedules();
            List<ScheduleSummary> schedules2 = dayScheduleDetail2.getSchedules();

            assertThat(dayScheduleDetails.size()).isEqualTo(2);
            assertThat(dayScheduleDetail1.getDayId()).isEqualTo(day1.getId());
            assertThat(dayScheduleDetail2.getDayId()).isEqualTo(day2.getId());
            assertThat(schedules1.size()).isEqualTo(3);
            assertThat(schedules2.size()).isEqualTo(1);
        }

        @Test
        void Day에_속하는_Schedule이_하나도_존재하지_않는_경우_ScheduleSummary_리스트의_크기는_0_이된다() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 5, 10);
            LocalDate endDate = LocalDate.of(2023, 5, 11);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);

            em.flush();
            em.clear();

            // when
            List<DayScheduleDetail> dayScheduleDetails = dayQueryDAOImpl.findDayScheduleListByTripId(trip.getId());
            List<ScheduleSummary> findSchedules = dayScheduleDetails.get(0).getSchedules();
            assertThat(findSchedules).isEmpty();
        }


        @Test
        @DisplayName("여행 날짜 기준 오름차순, 일정 순서값 기준 오름 차순으로 조회된다.")
        void sortTest() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 5, 10);
            LocalDate endDate = LocalDate.of(2023, 5, 11);


            Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
            em.persist(trip);

            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);
            em.persist(day2);  // 테스트의 편의를 위해 순서를 바꿔서 저장함.
            em.persist(day1);

            Schedule schedule1 = ScheduleFixture.day_NullId(trip, day1, 10000L);
            Schedule schedule2 = ScheduleFixture.day_NullId(trip, day1, 20000L);
            Schedule schedule3 = ScheduleFixture.day_NullId(trip, day1, 30000L);

            em.persist(schedule3); // 테스트의 편의를 위해 순서를 바꿔서 저장함. (3,1,2 순)
            em.persist(schedule1);
            em.persist(schedule2);
            em.flush();
            em.clear();

            // when
            List<DayScheduleDetail> dayScheduleDetails = dayQueryDAOImpl.findDayScheduleListByTripId(trip.getId());
            List<ScheduleSummary> findSchedules = dayScheduleDetails.get(0).getSchedules();

            // then
            assertThat(dayScheduleDetails.get(0).getDate()).isEqualTo(day1.getTripDate());
            assertThat(dayScheduleDetails.get(1).getDate()).isEqualTo(day2.getTripDate());
            assertThat(findSchedules.get(0).getScheduleId()).isEqualTo(schedule1.getId());
            assertThat(findSchedules.get(1).getScheduleId()).isEqualTo(schedule2.getId());
            assertThat(findSchedules.get(2).getScheduleId()).isEqualTo(schedule3.getId());

        }
    }

}
