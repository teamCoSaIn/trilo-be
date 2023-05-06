package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.vo.*;
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
                .title("여행 제목")
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule = Schedule.builder()
                .day(day)
                .trip(trip)
                .title("제목")
                .content("본문")
                .place(Place.of("place-id", "광안리 해수욕장", Coordinate.of(43.1275, 132.127)))
                .build();

        // when
        scheduleRepository.save(schedule);
        em.clear();

        // then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(findSchedule.getId()).isEqualTo(schedule.getId());
        assertThat(findSchedule.getTitle()).isEqualTo(schedule.getTitle());
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
                .title("여행 제목")
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule = trip.createSchedule(day, "일정1", Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
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
                .title("여행 제목")
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

        Schedule schedule1 = trip.createSchedule(day1, "일정1", Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = trip.createSchedule(day2, "일정2", Place.of("place-id2", "광화문 광장", Coordinate.of(37.5748, 126.9767)));
        Schedule schedule3 = trip.createSchedule(day3, "일정3", Place.of("place-id3", "도쿄 타워", Coordinate.of(35.3931, 139.4443)));

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
                .title("여행 제목")
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023, 3, 1), trip);
        em.persist(day);

        Schedule schedule1 = trip.createSchedule(day, "일정1", Place.of("place-id1", "광안리 해수욕장1", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = trip.createSchedule(day, "일정2", Place.of("place-id2", "광안리 해수욕장2", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule3 = trip.createSchedule(day, "일정3", Place.of("place-id3", "광안리 해수욕장3", Coordinate.of(35.1551, 129.1220)));

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
        assertThat(findSchedule.getTitle()).isEqualTo(schedule2.getTitle());
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
                    .title("여행 제목")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 1), trip);

            em.persist(day1);
            em.persist(day2);

            Schedule schedule1 = buildDummySchedule(trip, null, "일정제목1", Place.of("place-id1", "광안리 해수욕장1", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule2 = buildDummySchedule(trip, null, "일정제목2", Place.of("place-id2", "광안리 해수욕장2", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule3 = buildDummySchedule(trip, null, "일정제목3", Place.of("place-id3", "광안리 해수욕장3", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));
            Schedule schedule4 = buildDummySchedule(trip, day1, "일정제목4", Place.of("place-id4", "광안리 해수욕장4", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule5 = buildDummySchedule(trip, day1, "일정제목5", Place.of("place-id5", "광안리 해수욕장5", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule6 = buildDummySchedule(trip, day1, "일정제목6", Place.of("place-id6", "광안리 해수욕장6", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));
            Schedule schedule7 = buildDummySchedule(trip, day2, "일정제목7", Place.of("place-id7", "광안리 해수욕장7", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule8 = buildDummySchedule(trip, day2, "일정제목8", Place.of("place-id8", "광안리 해수욕장8", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule9 = buildDummySchedule(trip, day2, "일정제목9", Place.of("place-id9", "광안리 해수욕장9", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));

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
                    .title("여행 제목")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)))
                    .build();

            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);

            em.persist(day1);
            em.persist(day2);

            Schedule schedule1 = buildDummySchedule(trip, null, "일정제목1", Place.of("place-id1", "광안리 해수욕장1", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule2 = buildDummySchedule(trip, null, "일정제목2", Place.of("place-id2", "광안리 해수욕장2", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule3 = buildDummySchedule(trip, null, "일정제목3", Place.of("place-id3", "광안리 해수욕장3", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));
            Schedule schedule4 = buildDummySchedule(trip, day1, "일정제목4", Place.of("place-id4", "광안리 해수욕장4", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule5 = buildDummySchedule(trip, day1, "일정제목5", Place.of("place-id5", "광안리 해수욕장5", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule6 = buildDummySchedule(trip, day1, "일정제목6", Place.of("place-id6", "광안리 해수욕장6", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));
            Schedule schedule7 = buildDummySchedule(trip, day2, "일정제목7", Place.of("place-id7", "광안리 해수욕장7", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(7));
            Schedule schedule8 = buildDummySchedule(trip, day2, "일정제목8", Place.of("place-id8", "광안리 해수욕장8", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(-1));
            Schedule schedule9 = buildDummySchedule(trip, day2, "일정제목9", Place.of("place-id9", "광안리 해수욕장9", Coordinate.of(35.1551, 129.1220)), ScheduleIndex.of(5));

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
        private Schedule buildDummySchedule(Trip trip, Day day, String title, Place place, ScheduleIndex scheduleIndex) {
            return Schedule.builder()
                    .day(day)
                    .trip(trip)
                    .title(title)
                    .place(place)
                    .scheduleIndex(scheduleIndex)
                    .build();
        }
    }
}
