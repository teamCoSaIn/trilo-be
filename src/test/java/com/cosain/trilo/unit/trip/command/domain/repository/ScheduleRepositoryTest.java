package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.vo.Coordinate;
import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
                .tripPeriod(TripPeriod.of(LocalDate.of(2023,3,1), LocalDate.of(2023,3,1)))
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
                .tripPeriod(TripPeriod.of(LocalDate.of(2023,3,1), LocalDate.of(2023,3,1)))
                .build();

        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023,3,1), trip);
        em.persist(day);

        Schedule schedule = Schedule.create(day, trip, "일정1", Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
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
                .tripPeriod(TripPeriod.of(LocalDate.of(2023,3,1), LocalDate.of(2023,3,3)))
                .build();

        em.persist(trip);

        Day day1 = Day.of(LocalDate.of(2023,3,1), trip);
        Day day2 = Day.of(LocalDate.of(2023,3,2), trip);
        Day day3 = Day.of(LocalDate.of(2023,3,3), trip);

        em.persist(day1);
        em.persist(day2);
        em.persist(day3);

        Schedule schedule1 = Schedule.create(day1, trip, "일정1", Place.of("place-id1", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = Schedule.create(day2, trip, "일정2", Place.of("place-id2", "광화문 광장", Coordinate.of(37.5748, 126.9767)));
        Schedule schedule3 = Schedule.create(day3, trip, "일정3", Place.of("place-id3", "도쿄 타워", Coordinate.of(35.3931, 139.4443)));

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
                .tripPeriod(TripPeriod.of(LocalDate.of(2023,3,1), LocalDate.of(2023,3,1)))
                .build();
        em.persist(trip);

        Day day = Day.of(LocalDate.of(2023,3,1), trip);
        em.persist(day);

        Schedule schedule1 = Schedule.create(day, trip, "일정1", Place.of("place-id1", "광안리 해수욕장1", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule2 = Schedule.create(day, trip, "일정2", Place.of("place-id2", "광안리 해수욕장2", Coordinate.of(35.1551, 129.1220)));
        Schedule schedule3 = Schedule.create(day, trip, "일정3", Place.of("place-id3", "광안리 해수욕장3", Coordinate.of(35.1551, 129.1220)));

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

}
