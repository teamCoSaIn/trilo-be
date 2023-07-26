package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.repository.DayRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("DayRepositoryImpl 테스트")
public class DayRepositoryImplTest extends RepositoryTest {

    @Autowired
    private DayRepositoryImpl dayRepository;

    @Test
    @DisplayName("findBYWithTripTest - 같이 가져온 Trip이 실제 Trip 클래스인지 함께 검증")
    public void findByIdWithTripTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);

        Day day = trip.getDays().get(0);
        em.persist(day);

        em.flush();
        em.clear();

        // when
        Day findDay = dayRepository.findByIdWithTrip(day.getId()).get();

        // then
        Trip findDayTrip = findDay.getTrip();

        assertThat(findDay.getId()).isEqualTo(day.getId());
        assertThat(findDay.getTripDate()).isEqualTo(day.getTripDate());
        assertThat(findDayTrip.getId()).isEqualTo(trip.getId());
        assertThat(findDayTrip.getClass()).isSameAs(Trip.class);
    }

    @Test
    @DisplayName("deleteAllByIds- 전달받은 Id 목록의 Day들을 삭제")
    void deleteAllByIdsTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 4);
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);

        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        Day day3 = trip.getDays().get(2);
        Day day4 = trip.getDays().get(3);

        em.persist(day1);
        em.persist(day2);
        em.persist(day3);
        em.persist(day4);

        // when
        dayRepository.deleteAllByIds(List.of(day1.getId(), day2.getId()));

        // then
        List<Day> remainingDays = findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId(), day4.getId()));
        assertThat(remainingDays.size()).isEqualTo(2);
        assertThat(remainingDays).map(Day::getId).containsExactlyInAnyOrder(day3.getId(), day4.getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("deleteAllByTripId로 Day를 삭제하면, 해당 여행의 모든 Day들이 삭제된다.")
    void deleteAllByTripIdTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);

        em.persist(trip);

        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        Day day3 = trip.getDays().get(2);

        em.persist(day1);
        em.persist(day2);
        em.persist(day3);

        // when
        dayRepository.deleteAllByTripId(trip.getId());
        em.clear();

        // then
        List<Day> findDays = findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId()));
        assertThat(findDays).isEmpty();
    }

    @Nested
    class deleteAllByTripIdsTest {

        @Test
        void 전달받은_여행_ID_목록에_해당하는_모든_Day가_제거된다() {
            // given
            Long tripperId = setupTripperId();
            LocalDate commonStartDate = LocalDate.of(2023,3,1);
            LocalDate commonEndDate = LocalDate.of(2023,3,3);

            Trip trip1 = TripFixture.decided_nullId(tripperId, commonStartDate, commonEndDate);
            Trip trip2 = TripFixture.decided_nullId(tripperId, commonStartDate, commonEndDate);
            Trip trip3 = TripFixture.decided_nullId(tripperId, commonStartDate, commonEndDate);
            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);

            Day day1 = trip1.getDays().get(0);
            Day day2 = trip2.getDays().get(0);
            Day day3 = trip3.getDays().get(0);

            em.persist(day1);
            em.persist(day2);
            em.persist(day3);

            // when
            dayRepository.deleteAllByTripIds(List.of(trip1.getId(), trip2.getId(), trip3.getId()));

            // then
            List<Day> days = findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId()));
            assertThat(days).isEmpty();

        }
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

}
