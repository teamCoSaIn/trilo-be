package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.infra.repository.ScheduleRepositoryImpl;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.DEFAULT_SEQUENCE_GAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 일정 리포지토리 구현체({@link ScheduleRepositoryImpl}의 테스트 클래스입니다.
 * @see ScheduleRepositoryImpl
 */
@DisplayName("ScheduleRepositoryImpl 테스트")
public class ScheduleRepositoryImplTest extends RepositoryTest {

    /**
     * 테스트할 일정 리포지토리 구현체
     */
    @Autowired
    private ScheduleRepositoryImpl scheduleRepositoryImpl;

    @Test
    @DisplayName("Schedule을 저장하고 같은 식별자로 찾으면 같은 Schedule이 찾아진다.")
    void saveTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);
        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day = trip.getDays().get(0);

        Schedule schedule = ScheduleFixture.day_NullId(trip, day, 0L);

        // when
        scheduleRepositoryImpl.save(schedule);
        flushAndClear();

        // then
        Schedule findSchedule = scheduleRepositoryImpl.findById(schedule.getId()).get();
        assertThat(findSchedule.getId()).isEqualTo(schedule.getId());
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(schedule.getScheduleTitle());
        assertThat(findSchedule.getScheduleContent()).isEqualTo(schedule.getScheduleContent());
        assertThat(findSchedule.getScheduleIndex()).isEqualTo(schedule.getScheduleIndex());
        assertThat(findSchedule.getPlace()).isEqualTo(schedule.getPlace());
        assertThat(findSchedule.getScheduleTime()).isEqualTo(schedule.getScheduleTime());
    }

    @Test
    @DisplayName("일정 본문을 65535 바이트보다 큰 일정 본문으로 수정 시도하면 데이터베이스 예외가 발생함")
    void contentChangeConstraintsTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day = trip.getDays().get(0);
        Schedule schedule = setupDaySchedule(trip, day, 0L);

        byte[] bytes = new byte[65536]; // 65535 바이트를 넘어가는 데이터
        Arrays.fill(bytes, (byte) 'A'); // 1바이트 'A'로 채움
        String rawContent = new String(bytes, StandardCharsets.UTF_8); // 65536 바이트의 텍스트

        Query query = em.createQuery("""
                        UPDATE Schedule s
                        SET s.scheduleContent.value = :rawContent
                        where s.id = :scheduleId
                        """)
                .setParameter("rawContent", rawContent)
                .setParameter("scheduleId", schedule.getId());

        // when & then
        assertThatThrownBy(query::executeUpdate).isInstanceOf(PersistenceException.class);
    }


    @Test
    @DisplayName("delete로 일정을 삭제하면, 해당 일정이 삭제된다.")
    void deleteTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day = trip.getDays().get(0);

        Schedule schedule = setupDaySchedule(trip, day, 0L);

        // when
        scheduleRepositoryImpl.delete(schedule);
        em.flush();
        em.clear();

        // then
        Schedule findSchedule = scheduleRepositoryImpl.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNull();
    }

    /**
     * {@link ScheduleRepositoryImpl#deleteAllByTripId(Long)} 메서드 실행 시
     * 여행의 ID에 해당하는 일정을 모두 삭제할 수 있는 지 테스트합니다.
     * @see ScheduleRepositoryImpl#deleteAllByTripId(Long)
     */
    @Test
    @DisplayName("deleteAllByTripId로 일정을 삭제하면, 해당 여행의 모든 일정들이 삭제된다.")
    void deleteAllByTripIdTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        Day day3 = trip.getDays().get(2);

        Schedule schedule1 = setupDaySchedule(trip, day1, 0L);
        Schedule schedule2 = setupDaySchedule(trip, day2, 0L);
        Schedule schedule3 = setupDaySchedule(trip, day3, 0L);
        Schedule schedule4 = setupTemporarySchedule(trip, 0L);
        flushAndClear(); // 여행, Day, 일정 생성

        // when
        scheduleRepositoryImpl.deleteAllByTripId(trip.getId()); // Trip Id에 속한 모든 일정 삭제
        flushAndClear();

        // then
        List<Schedule> findSchedules = findAllScheduleByIds(List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(), schedule4.getId()));
        assertThat(findSchedules).isEmpty(); // 조회 시 일정들 모두 삭제됨 확인
    }


    @Test
    @DisplayName("findByIdWithTrip으로 일정을 조회하면 해당 일정만 조회된다.(여행도 같이 묶여서 조회됨)")
    void findByIdWithTripTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day = trip.getDays().get(0);

        Schedule schedule1 = setupDaySchedule(trip, day, 0L);
        Schedule schedule2 = setupDaySchedule(trip, day, 100L);
        Schedule schedule3 = setupDaySchedule(trip, day, 200L);
        flushAndClear();

        // when
        Schedule findSchedule = scheduleRepositoryImpl.findByIdWithTrip(schedule2.getId()).get();

        // then
        assertThat(findSchedule.getId()).isEqualTo(schedule2.getId());
        assertThat(findSchedule.getTrip().getClass()).isEqualTo(Trip.class);
        assertThat(findSchedule.getTrip().getId()).isEqualTo(trip.getId());
    }


    @Nested
    @DisplayName("relocateSchedules 테스트")
    class RelocateSchedulesTest {

        @DisplayName("임시보관함의 일정 재갱신 -> 임시보관함만 재갱신됨")
        @Test
        void relocateTemporaryStorage() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            Schedule schedule1 = setupTemporarySchedule(trip, 7L);
            Schedule schedule2 = setupTemporarySchedule(trip, -1L);
            Schedule schedule3 = setupTemporarySchedule(trip, 5L);
            Schedule schedule4 = setupDaySchedule(trip, day1, 7L);
            Schedule schedule5 = setupDaySchedule(trip, day1, -1L);
            Schedule schedule6 = setupDaySchedule(trip, day1, 5L);
            Schedule schedule7 = setupDaySchedule(trip, day2, 7L);
            Schedule schedule8 = setupDaySchedule(trip, day2, -1L);
            Schedule schedule9 = setupDaySchedule(trip, day2, 5L);

            // when
            int affectedRowCount = scheduleRepositoryImpl.relocateDaySchedules(trip.getId(), null); // 임시보관함 재배치

            // then
            List<Schedule> schedules = findAllScheduleByIds(
                    List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(),
                            schedule4.getId(), schedule5.getId(), schedule6.getId(),
                            schedule7.getId(), schedule8.getId(), schedule9.getId()));

            assertThat(affectedRowCount).isEqualTo(3);
            assertThat(schedules).map(schedule -> schedule.getScheduleIndex().getValue())
                    .containsExactly(
                            DEFAULT_SEQUENCE_GAP * 2, 0L, DEFAULT_SEQUENCE_GAP,
                            7L, -1L, 5L,
                            7L, -1L, 5L);
        }

        @DisplayName("day의 일정 재갱신 -> 해당 day만 재갱신됨")
        @Test
        void relocateDaySchedules() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            Schedule schedule1 = setupTemporarySchedule(trip, 7L);
            Schedule schedule2 = setupTemporarySchedule(trip, -1L);
            Schedule schedule3 = setupTemporarySchedule(trip, 5L);
            Schedule schedule4 = setupDaySchedule(trip, day1, 7L);
            Schedule schedule5 = setupDaySchedule(trip, day1, -1L);
            Schedule schedule6 = setupDaySchedule(trip, day1, 5L);
            Schedule schedule7 = setupDaySchedule(trip, day2, 7L);
            Schedule schedule8 = setupDaySchedule(trip, day2, -1L);
            Schedule schedule9 = setupDaySchedule(trip, day2, 5L);

            // when
            int affectedRowCount = scheduleRepositoryImpl.relocateDaySchedules(trip.getId(), day1.getId());

            // then
            List<Schedule> schedules = findAllScheduleByIds(
                    List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(),
                            schedule4.getId(), schedule5.getId(), schedule6.getId(),
                            schedule7.getId(), schedule8.getId(), schedule9.getId()));

            assertThat(affectedRowCount).isEqualTo(3);
            assertThat(schedules).map(schedule -> schedule.getScheduleIndex().getValue())
                    .containsExactly(
                            7L, -1L, 5L,
                            DEFAULT_SEQUENCE_GAP * 2, 0L, DEFAULT_SEQUENCE_GAP,
                            7L, -1L, 5L);
        }

    }


    @Nested
    @DisplayName("moveSchedulesToTemporaryStorage (day들 일괄 임시보관함 이동)")
    class MoveScheduleToTemporaryStorage {

        @Test
        @DisplayName("임시보관함에 다른 일정이 있으면, 맨 뒤 순서값 뒤에 day들의 일정들이 date, 순서값 순으로 오름차순으로 옮겨짐")
        public void test_When_TemporaryStorage_not_empty() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 3);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);
            Day day3 = trip.getDays().get(2);

            Schedule schedule1 = setupTemporarySchedule(trip, 0);
            Schedule schedule2 = setupTemporarySchedule(trip, DEFAULT_SEQUENCE_GAP);
            Schedule schedule3 = setupDaySchedule(trip, day2, 2);
            Schedule schedule4 = setupDaySchedule(trip, day2, 1);
            Schedule schedule5 = setupDaySchedule(trip, day1, 2);
            Schedule schedule6 = setupDaySchedule(trip, day1, 1);
            Schedule schedule7 = setupDaySchedule(trip, day3, 2);
            Schedule schedule8 = setupDaySchedule(trip, day3, 1);

            // when
            int affectedRowCount = scheduleRepositoryImpl.moveSchedulesToTemporaryStorage(trip.getId(), List.of(day1.getId(), day2.getId()));

            // then
            Schedule findSchedule1 = em.find(Schedule.class, schedule1.getId());
            Schedule findSchedule2 = em.find(Schedule.class, schedule2.getId());
            Schedule findSchedule3 = em.find(Schedule.class, schedule3.getId());
            Schedule findSchedule4 = em.find(Schedule.class, schedule4.getId());
            Schedule findSchedule5 = em.find(Schedule.class, schedule5.getId());
            Schedule findSchedule6 = em.find(Schedule.class, schedule6.getId());
            Schedule findSchedule7 = em.find(Schedule.class, schedule7.getId());
            Schedule findSchedule8 = em.find(Schedule.class, schedule8.getId());

            assertThat(affectedRowCount).isEqualTo(4);
            assertThat(findSchedule1.getDay()).isEqualTo(null);
            assertThat(findSchedule2.getDay()).isEqualTo(null);
            assertThat(findSchedule3.getDay()).isEqualTo(null);
            assertThat(findSchedule4.getDay()).isEqualTo(null);
            assertThat(findSchedule5.getDay()).isEqualTo(null);
            assertThat(findSchedule6.getDay()).isEqualTo(null);
            assertThat(findSchedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(0));
            assertThat(findSchedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP));
            assertThat(findSchedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 5));
            assertThat(findSchedule4.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 4));
            assertThat(findSchedule5.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 3));
            assertThat(findSchedule6.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 2));
            assertThat(findSchedule7.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule8.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule7.getScheduleIndex()).isEqualTo(ScheduleIndex.of(2));
            assertThat(findSchedule8.getScheduleIndex()).isEqualTo(ScheduleIndex.of(1));
        }

        @Test
        @DisplayName("임시보관함이 비어있으면, day들의 일정들이 date, 순서값 순으로 오름차순으로 0번 순서부터 지정되어 옮겨짐")
        public void test_When_TemporaryStorage_empty() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 3);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);
            Day day3 = trip.getDays().get(2);

            Schedule schedule1 = setupDaySchedule(trip, day2, 2);
            Schedule schedule2 = setupDaySchedule(trip, day2, 1);
            Schedule schedule3 = setupDaySchedule(trip, day1, 2);
            Schedule schedule4 = setupDaySchedule(trip, day1, 1);
            Schedule schedule5 = setupDaySchedule(trip, day3, 2);
            Schedule schedule6 = setupDaySchedule(trip, day3, 1);

            // when
            int affectedRowCount = scheduleRepositoryImpl.moveSchedulesToTemporaryStorage(trip.getId(), List.of(day1.getId(), day2.getId()));

            // then
            Schedule findSchedule1 = em.find(Schedule.class, schedule1.getId());
            Schedule findSchedule2 = em.find(Schedule.class, schedule2.getId());
            Schedule findSchedule3 = em.find(Schedule.class, schedule3.getId());
            Schedule findSchedule4 = em.find(Schedule.class, schedule4.getId());
            Schedule findSchedule5 = em.find(Schedule.class, schedule5.getId());
            Schedule findSchedule6 = em.find(Schedule.class, schedule6.getId());

            assertThat(affectedRowCount).isEqualTo(4);
            assertThat(findSchedule1.getDay()).isEqualTo(null);
            assertThat(findSchedule2.getDay()).isEqualTo(null);
            assertThat(findSchedule3.getDay()).isEqualTo(null);
            assertThat(findSchedule4.getDay()).isEqualTo(null);
            assertThat(findSchedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 3));
            assertThat(findSchedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP * 2));
            assertThat(findSchedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(DEFAULT_SEQUENCE_GAP));
            assertThat(findSchedule4.getScheduleIndex()).isEqualTo(ScheduleIndex.of(0));
            assertThat(findSchedule5.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule6.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule5.getScheduleIndex()).isEqualTo(ScheduleIndex.of(2));
            assertThat(findSchedule6.getScheduleIndex()).isEqualTo(ScheduleIndex.of(1));
        }
    }

    @Nested
    @DisplayName("findTripScheduleCount : 여행에 속한 일정의 갯수를 가져온다.")
    class FindTripScheduleCountTest {

        @DisplayName("아무 일정도 없을 때 0 반환")
        @Test
        public void emptyScheduleTest() {
            Long tripperId = setupTripperId();
            Trip trip = setupUndecidedTrip(tripperId);

            int scheduleTripCount = scheduleRepositoryImpl.findTripScheduleCount(trip.getId());
            assertThat(scheduleTripCount).isEqualTo(0);
        }

        @DisplayName("임시보관함 일정 2개 -> 2 반환")
        @Test
        public void temporaryStorageScheduleTest() {
            Long tripperId = setupTripperId();
            Trip trip = setupUndecidedTrip(tripperId);

            Schedule schedule1 = setupTemporarySchedule(trip, 0L);
            Schedule schedule2 = setupTemporarySchedule(trip, 100L);
            flushAndClear();

            int scheduleTripCount = scheduleRepositoryImpl.findTripScheduleCount(trip.getId());
            assertThat(scheduleTripCount).isEqualTo(2);
        }

        @DisplayName("Trip의 어떤 Day에 일정 3개 -> 3 반환")
        @Test
        public void dayScheduleScheduleTest() {
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 1);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day = trip.getDays().get(0);

            Schedule schedule1 = setupDaySchedule(trip, day, 0L);
            Schedule schedule2 = setupDaySchedule(trip, day, 100L);
            Schedule schedule3 = setupDaySchedule(trip, day, 200L);
            flushAndClear();

            int scheduleTripCount = scheduleRepositoryImpl.findTripScheduleCount(trip.getId());
            assertThat(scheduleTripCount).isEqualTo(3);
        }

        @DisplayName("Trip의 임시보관함, 여러 Day에 일정 -> 여행 소속 일정 갯수 반환")
        @Test
        public void manyDayScheduleTest() {
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 3);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);
            Day day3 = trip.getDays().get(2);

            Schedule schedule1 = setupDaySchedule(trip, day1, 0L);
            Schedule schedule2 = setupDaySchedule(trip, day2, 100L);
            Schedule schedule3 = setupDaySchedule(trip, day3, 200L);
            Schedule schedule4 = setupTemporarySchedule(trip, 0L);
            flushAndClear();

            int scheduleTripCount = scheduleRepositoryImpl.findTripScheduleCount(trip.getId());
            assertThat(scheduleTripCount).isEqualTo(4);
        }

    }

    @Nested
    @DisplayName("findDayScheduleCount : Day에 속한 일정의 갯수를 가져온다.")
    class FindDayScheduleCountTest {

        @DisplayName("Day에 아무 일정도 없음 -> 0 반환")
        @Test
        void noDayScheduleTest() {
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 1);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day = trip.getDays().get(0);
            flushAndClear();

            int scheduleTripCount = scheduleRepositoryImpl.findDayScheduleCount(day.getId());
            assertThat(scheduleTripCount).isEqualTo(0);
        }

        @DisplayName("Day에 일정 3개 -> 3 반환")
        @Test
        void threeDayScheduleTest() {
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            Schedule schedule1 = setupDaySchedule(trip, day1, 0L);
            Schedule schedule2 = setupDaySchedule(trip, day1, 100L);
            Schedule schedule3 = setupDaySchedule(trip, day1, 200L);
            Schedule schedule4 = setupDaySchedule(trip, day2, 0L);
            Schedule schedule5 = setupDaySchedule(trip, day2, 100L);
            flushAndClear();

            int dayScheduleCount = scheduleRepositoryImpl.findDayScheduleCount(day1.getId());
            assertThat(dayScheduleCount).isEqualTo(3);
        }
    }

    @Nested
    class deleteAllByTripIdsTest {
        @Test
        void 전달받은_여행_ID_목록에_해당하는_모든_일정이_삭제된다() {
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 2);

            Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            Schedule schedule1 = setupDaySchedule(trip, day1, 0L);
            Schedule schedule2 = setupDaySchedule(trip, day1, 100L);
            Schedule schedule3 = setupTemporarySchedule(trip, 0L);
            Schedule schedule4 = setupTemporarySchedule(trip, 100L);
            Schedule schedule5 = setupDaySchedule(trip, day2, 100L);

            // when
            scheduleRepositoryImpl.deleteAllByTripIds(List.of(trip.getId()));

            // then
            List<Schedule> findSchedules = findAllScheduleByIds(List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(), schedule4.getId(), schedule5.getId()));
            assertThat(findSchedules).isEmpty();
        }
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
