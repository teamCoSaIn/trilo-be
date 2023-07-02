package com.cosain.trilo.unit.trip.domain.repository;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
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
@DisplayName("[TripCommand] TripRepository 테스트")
public class TripRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TestEntityManager em;

    @Nested
    @DisplayName("Trip 저장 후 FindById로 조회")
    class saveAndFindByIdTest {

        @Test
        @DisplayName("같은 id로 조회하면 같은 여행이 찾아진다.")
        void successTest() {
            Trip trip = Trip.create(TripTitle.of("제목"), 1L);
            tripRepository.save(trip);

            em.clear();

            Trip findTrip = tripRepository.findById(trip.getId()).get();
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getTripperId()).isEqualTo(trip.getTripperId());
            assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());
        }

        @Test
        @DisplayName("임시보관함을 지연로딩(기본 양방향 매핑)하여 얻어오면, 순서대로 요소들이 가져와진다.")
        void lazy_loading_TemporaryStorage() {
            // given
            Trip trip = Trip.create(TripTitle.of("제목"), 1L);
            tripRepository.save(trip);

            Schedule schedule1 = Schedule.builder()
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정1"))
                    .place(Place.of("place-id1", "광안리 해수욕장111", Coordinate.of(35.1551, 129.1220)))
                    .scheduleIndex(ScheduleIndex.of(30_000_000L))
                    .build();

            Schedule schedule2 = Schedule.builder()
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정2"))
                    .place(Place.of("place-id2", "광안리 해수욕장222", Coordinate.of(35.1551, 129.1220)))
                    .scheduleIndex(ScheduleIndex.of(50_000_000L))
                    .build();


            Schedule schedule3 = Schedule.builder()
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정3"))
                    .place(Place.of("place-id3", "광안리 해수욕장333", Coordinate.of(35.1551, 129.1220)))
                    .scheduleIndex(ScheduleIndex.of(-10_000_000L))
                    .build();

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);

            em.clear();

            // when
            Trip findTrip = tripRepository.findById(trip.getId()).get();
            List<Schedule> temporaryStorage = findTrip.getTemporaryStorage();

            // then
            assertThat(temporaryStorage).map(Schedule::getScheduleIndex)
                    .containsExactly(ScheduleIndex.of(-10_000_000L), ScheduleIndex.of(30_000_000L), ScheduleIndex.of(50_000_000L));
        }
    }

    @Nested
    @DisplayName("findByIdWithDays")
    class FindByIdWithDaysTest {

        @Test
        @DirtiesContext
        @DisplayName("UnDecided 상태의 Trip을 조회하면 여행만 조회된다.")
        public void findUndecidedTripTest() {
            // given
            Trip trip = Trip.create(TripTitle.of("제목"), 1L);
            em.persist(trip);

            em.clear();

            // when
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getDays()).isEmpty();
        }

        @Test
        @DirtiesContext
        @DisplayName("Day를 가지고 있는 Trip을 조회하면 Trip이 Day들을 가진 채 조회된다.")
        void if_trip_has_days_then_trip_and_its_trip_found(){
            // given
            Trip trip = TripFixture.DECIDED_TRIP.createDecided(null, 1L, "여행 제목", LocalDate.of(2023,5,1), LocalDate.of(2023,5,3));
            em.persist(trip);

            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);
            Day day3 = trip.getDays().get(2);
            em.persist(day1);
            em.persist(day2);
            em.persist(day3);

            em.clear();

            // when
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getDays()).map(Day::getTripDate).containsExactly(
                    LocalDate.of(2023,5,1), LocalDate.of(2023,5,2), LocalDate.of(2023,5,3));
        }
    }

    @Test
    @DirtiesContext
    @DisplayName("delete 테스트")
    public void deleteTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023,3,1), LocalDate.of(2023,3,3)))
                .build();

        em.persist(trip);

        // when
        tripRepository.delete(trip);
        em.flush();
        em.clear();

        // then
        Trip findTrip = tripRepository.findById(trip.getId()).orElse(null);
        assertThat(findTrip).isNull();
    }

    @Nested
    class deleteAllByTripperIdTest{
        @Test
        void tripperId_에_해당하는_모든_trip이_제거된다(){
            // given
            Long tripperId = 1L;
            Trip trip1 = Trip.create(TripTitle.of("여행 제목1"), tripperId);
            Trip trip2 = Trip.create(TripTitle.of("여행 제목2"), tripperId);
            Trip trip3 = Trip.create(TripTitle.of("여행 제목3"), tripperId);
            Trip trip4 = Trip.create(TripTitle.of("여행 제목4"), tripperId);
            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);
            em.persist(trip4);

            em.flush();
            em.clear();

            // when
            tripRepository.deleteAllByTripperId(tripperId);

            // then
            assertThat(tripRepository.existsById(trip1.getId())).isFalse();
            assertThat(tripRepository.existsById(trip2.getId())).isFalse();
            assertThat(tripRepository.existsById(trip3.getId())).isFalse();
            assertThat(tripRepository.existsById(trip4.getId())).isFalse();
        }
    }
}
