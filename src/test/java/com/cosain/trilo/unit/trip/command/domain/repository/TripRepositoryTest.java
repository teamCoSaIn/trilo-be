package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("[TripCommand] TripRepository 테스트")
public class TripRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DirtiesContext
    @DisplayName("Trip을 저장하고 같은 식별자로 찾으면 같은 Trip이 찾아진다.")
    void saveTest() {
        Trip trip = Trip.create("제목", 1L);
        tripRepository.save(trip);

        em.clear();

        Trip findTrip = tripRepository.findById(trip.getId()).get();
        assertThat(findTrip.getId()).isEqualTo(trip.getId());
        assertThat(findTrip.getTripperId()).isEqualTo(trip.getTripperId());
        assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());
    }

    @Nested
    @DisplayName("findByIdWithDays")
    class FindByIdWithDaysTest {

        @Test
        @DirtiesContext
        @DisplayName("UnDecided 상태의 Trip을 조회하면 여행만 조회된다.")
        public void findUndecidedTripTest() {
            // given
            Trip trip = Trip.create("제목", 1L);
            em.persist(trip);

            em.clear();

            // when
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTitle()).isEqualTo(trip.getTitle());
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getDays()).isEmpty();
        }

        @Test
        @DirtiesContext
        @DisplayName("Day를 가지고 있는 Trip을 조회하면 Trip이 Day들을 가진 채 조회된다.")
        void if_trip_has_days_then_trip_and_its_trip_found(){
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023,5,1), LocalDate.of(2023,5,3)))
                    .status(TripStatus.DECIDED)
                    .build();

            em.persist(trip);

            Day day1 = Day.of(LocalDate.of(2023, 5, 1), trip);
            Day day2 = Day.of(LocalDate.of(2023, 5, 2), trip);
            Day day3 = Day.of(LocalDate.of(2023, 5, 3), trip);

            em.persist(day1);
            em.persist(day2);
            em.persist(day3);

            em.clear();

            // when
            Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTitle()).isEqualTo(trip.getTitle());
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
                .title("여행 제목")
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
}
