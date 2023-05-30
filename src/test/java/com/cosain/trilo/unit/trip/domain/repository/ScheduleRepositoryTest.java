package com.cosain.trilo.unit.trip.domain.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("[TripCommand] ScheduleRepository 테스트")
public class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TestEntityManager em;


    @Test
    @DirtiesContext
    @DisplayName("Schedule을 저장하고 같은 식별자로 찾으면 같은 Schedule이 찾아진다.")
    void saveTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule = Schedule.builder()
                .day(day)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("제목"))
                .content("본문")
                .place(Place.of("place-id", "광안리 해수욕장", Coordinate.of(43.1275, 132.127)))
                .build();

        // when
        scheduleRepository.save(schedule);
        em.clear();

        // then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(findSchedule.getId()).isEqualTo(schedule.getId());
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(schedule.getScheduleTitle());
        assertThat(findSchedule.getContent()).isEqualTo(schedule.getContent());
        assertThat(findSchedule.getPlace()).isEqualTo(schedule.getPlace());
    }


    @Test
    @DirtiesContext
    @DisplayName("delete로 일정을 삭제하면, 해당 일정이 삭제된다.")
    void deleteTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule = trip.createSchedule(day, ScheduleTitle.of("일정1"), Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
        em.persist(schedule);

        // when
        scheduleRepository.delete(schedule);
        em.flush();
        em.clear();

        // then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNull();
    }


    @Test
    @DirtiesContext
    @DisplayName("deleteAllByTripId로 일정을 삭제하면, 해당 여행의 모든 일정들이 삭제된다.")
    void deleteAllByTripIdTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .tripTitle(TripTitle.of(("여행 제목")))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3)))
                .build();

        em.persist(trip);

        Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
        Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);
        Day day3 = Day.of(LocalDate.of(2023, 3, 3), trip);

        em.persist(day1);
        em.persist(day2);
        em.persist(day3);

        Schedule schedule1 = trip.createSchedule(day1, ScheduleTitle.of("일정1"), Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = trip.createSchedule(day2, ScheduleTitle.of("일정2"), Place.of("place-id2", "광화문 광장", Coordinate.of(37.5748, 126.9767)));
        Schedule schedule3 = trip.createSchedule(day3, ScheduleTitle.of("일정3"), Place.of("place-id3", "도쿄 타워", Coordinate.of(35.3931, 139.4443)));

        em.persist(schedule1);
        em.persist(schedule2);
        em.persist(schedule3);

        // when
        scheduleRepository.deleteAllByTripId(trip.getId());
        em.clear();

        // then
        List<Schedule> findSchedules = scheduleRepository.findAllById(List.of(schedule1.getId(), schedule2.getId(), schedule3.getId()));
        assertThat(findSchedules).isEmpty();
    }


    @Test
    @DirtiesContext
    @DisplayName("findByIdWithTrip으로 일정을 조회하면 해당 일정만 조회된다.(여행도 같이 묶여서 조회됨)")
    void findByIdWithTripTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정1"), Place.of("place-id1", "광안리 해수욕장1", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정2"), Place.of("place-id2", "광안리 해수욕장2", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule3 = trip.createSchedule(day, ScheduleTitle.of("일정3"), Place.of("place-id3", "광안리 해수욕장3", Coordinate.of(35.1551, 129.1220)));

        em.persist(schedule1);
        em.persist(schedule2);
        em.persist(schedule3);
        em.flush();
        em.clear();

        // when
        Schedule findSchedule = scheduleRepository.findByIdWithTrip(schedule2.getId()).get();

        // then
        assertThat(findSchedule.getId()).isEqualTo(schedule2.getId());
        assertThat(findSchedule.getTrip().getId()).isEqualTo(trip.getId());
        assertThat(findSchedule.getScheduleTitle()).isEqualTo(schedule2.getScheduleTitle());
        assertThat(findSchedule.getPlace()).isEqualTo(schedule2.getPlace());
    }


    @Nested
    @DisplayName("relocateSchedules 테스트")
    class RelocateSchedulesTest {

        @DisplayName("임시보관함의 일정 재갱신 -> 임시보관함만 재갱신됨")
        @Test
        void relocateTemporaryStorage() {
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 1), trip);

            em.persist(day1);
            em.persist(day2);

            Schedule schedule1 = buildDummySchedule(trip, null, ScheduleIndex.of(7));
            Schedule schedule2 = buildDummySchedule(trip, null, ScheduleIndex.of(-1));
            Schedule schedule3 = buildDummySchedule(trip, null, ScheduleIndex.of(5));
            Schedule schedule4 = buildDummySchedule(trip, day1, ScheduleIndex.of(7));
            Schedule schedule5 = buildDummySchedule(trip, day1, ScheduleIndex.of(-1));
            Schedule schedule6 = buildDummySchedule(trip, day1, ScheduleIndex.of(5));
            Schedule schedule7 = buildDummySchedule(trip, day2, ScheduleIndex.of(7));
            Schedule schedule8 = buildDummySchedule(trip, day2, ScheduleIndex.of(-1));
            Schedule schedule9 = buildDummySchedule(trip, day2, ScheduleIndex.of(5));

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.persist(schedule4);
            em.persist(schedule5);
            em.persist(schedule6);
            em.persist(schedule7);
            em.persist(schedule8);
            em.persist(schedule9);


            // when
            int affectedRowCount = scheduleRepository.relocateDaySchedules(trip.getId(), null);

            // then
            List<Schedule> schedules = scheduleRepository.findAllById(
                    List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(),
                            schedule4.getId(), schedule5.getId(), schedule6.getId(),
                            schedule7.getId(), schedule8.getId(), schedule9.getId()));

            assertThat(affectedRowCount).isEqualTo(3);
            assertThat(schedules).map(Schedule::getScheduleIndex)
                    .containsExactly(
                            ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2), ScheduleIndex.ZERO_INDEX, ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP),
                            ScheduleIndex.of(7), ScheduleIndex.of(-1), ScheduleIndex.of(5),
                            ScheduleIndex.of(7), ScheduleIndex.of(-1), ScheduleIndex.of(5)
                    );
        }

        @DisplayName("day의 일정 재갱신 -> 해당 day만 재갱신됨")
        @Test
        void relocateDaySchedules() {
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)))
                    .build();

            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);

            em.persist(day1);
            em.persist(day2);

            Schedule schedule1 = buildDummySchedule(trip, null, ScheduleIndex.of(7));
            Schedule schedule2 = buildDummySchedule(trip, null, ScheduleIndex.of(-1));
            Schedule schedule3 = buildDummySchedule(trip, null, ScheduleIndex.of(5));
            Schedule schedule4 = buildDummySchedule(trip, day1, ScheduleIndex.of(7));
            Schedule schedule5 = buildDummySchedule(trip, day1, ScheduleIndex.of(-1));
            Schedule schedule6 = buildDummySchedule(trip, day1, ScheduleIndex.of(5));
            Schedule schedule7 = buildDummySchedule(trip, day2, ScheduleIndex.of(7));
            Schedule schedule8 = buildDummySchedule(trip, day2, ScheduleIndex.of(-1));
            Schedule schedule9 = buildDummySchedule(trip, day2, ScheduleIndex.of(5));

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.persist(schedule4);
            em.persist(schedule5);
            em.persist(schedule6);
            em.persist(schedule7);
            em.persist(schedule8);
            em.persist(schedule9);

            // when
            int affectedRowCount = scheduleRepository.relocateDaySchedules(trip.getId(), day1.getId());

            // then
            List<Schedule> schedules = scheduleRepository.findAllById(
                    List.of(schedule1.getId(), schedule2.getId(), schedule3.getId(),
                            schedule4.getId(), schedule5.getId(), schedule6.getId(),
                            schedule7.getId(), schedule8.getId(), schedule9.getId()));

            assertThat(affectedRowCount).isEqualTo(3);
            assertThat(schedules).map(Schedule::getScheduleIndex)
                    .containsExactly(
                            ScheduleIndex.of(7), ScheduleIndex.of(-1), ScheduleIndex.of(5),
                            ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2), ScheduleIndex.ZERO_INDEX, ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP),
                            ScheduleIndex.of(7), ScheduleIndex.of(-1), ScheduleIndex.of(5)
                    );
        }

    }


    @Nested
    @DisplayName("moveSchedulesToTemporaryStorage (day들 일괄 임시보관함 이동)")
    class MoveScheduleToTemporaryStorage {

        @Test
        @DisplayName("임시보관함에 다른 일정이 있으면, 맨 뒤 순서값 뒤에 day들의 일정들이 date, 순서값 순으로 오름차순으로 옮겨짐")
        public void test_When_TemporaryStorage_not_empty() {
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3)))
                    .build();
            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);
            Day day3 = Day.of(LocalDate.of(2023, 3, 3), trip);
            em.persist(day1);
            em.persist(day2);
            em.persist(day3);

            Schedule schedule1 = buildDummySchedule(trip, null, ScheduleIndex.of(0));
            Schedule schedule2 = buildDummySchedule(trip, null, ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
            Schedule schedule3 = buildDummySchedule(trip, day2, ScheduleIndex.of(2));
            Schedule schedule4 = buildDummySchedule(trip, day2, ScheduleIndex.of(1));
            Schedule schedule5 = buildDummySchedule(trip, day1, ScheduleIndex.of(2));
            Schedule schedule6 = buildDummySchedule(trip, day1, ScheduleIndex.of(1));
            Schedule schedule7 = buildDummySchedule(trip, day3, ScheduleIndex.of(2));
            Schedule schedule8 = buildDummySchedule(trip, day3, ScheduleIndex.of(1));

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.persist(schedule4);
            em.persist(schedule5);
            em.persist(schedule6);
            em.persist(schedule7);
            em.persist(schedule8);

            // when
            int affectedRowCount = scheduleRepository.moveSchedulesToTemporaryStorage(trip.getId(), List.of(day1.getId(), day2.getId()));

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
            assertThat(findSchedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
            assertThat(findSchedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 5));
            assertThat(findSchedule4.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 4));
            assertThat(findSchedule5.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 3));
            assertThat(findSchedule6.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2));
            assertThat(findSchedule7.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule8.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule7.getScheduleIndex()).isEqualTo(ScheduleIndex.of(2));
            assertThat(findSchedule8.getScheduleIndex()).isEqualTo(ScheduleIndex.of(1));
        }

        @Test
        @DisplayName("임시보관함이 비어있으면, day들의 일정들이 date, 순서값 순으로 오름차순으로 0번 순서부터 지정되어 옮겨짐")
        public void test_When_TemporaryStorage_empty() {
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3)))
                    .build();
            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);
            Day day3 = Day.of(LocalDate.of(2023, 3, 3), trip);
            em.persist(day1);
            em.persist(day2);
            em.persist(day3);

            Schedule schedule1 = buildDummySchedule(trip, day2, ScheduleIndex.of(2));
            Schedule schedule2 = buildDummySchedule(trip, day2, ScheduleIndex.of(1));
            Schedule schedule3 = buildDummySchedule(trip, day1, ScheduleIndex.of(2));
            Schedule schedule4 = buildDummySchedule(trip, day1, ScheduleIndex.of(1));
            Schedule schedule5 = buildDummySchedule(trip, day3, ScheduleIndex.of(2));
            Schedule schedule6 = buildDummySchedule(trip, day3, ScheduleIndex.of(1));

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.persist(schedule4);
            em.persist(schedule5);
            em.persist(schedule6);

            // when
            int affectedRowCount = scheduleRepository.moveSchedulesToTemporaryStorage(trip.getId(), List.of(day1.getId(), day2.getId()));

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
            assertThat(findSchedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 3));
            assertThat(findSchedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2));
            assertThat(findSchedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
            assertThat(findSchedule4.getScheduleIndex()).isEqualTo(ScheduleIndex.of(0));
            assertThat(findSchedule5.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule6.getDay().getId()).isEqualTo(day3.getId());
            assertThat(findSchedule5.getScheduleIndex()).isEqualTo(ScheduleIndex.of(2));
            assertThat(findSchedule6.getScheduleIndex()).isEqualTo(ScheduleIndex.of(1));
        }
    }


    private Schedule buildDummySchedule(Trip trip, Day day, ScheduleIndex scheduleIndex) {
        return Schedule.builder()
                .day(day)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("place-id", "더미 제목", Coordinate.of(35.1551, 129.1220)))
                .scheduleIndex(scheduleIndex)
                .build();
    }

}
