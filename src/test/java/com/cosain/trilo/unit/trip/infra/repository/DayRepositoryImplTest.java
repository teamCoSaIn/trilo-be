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

/**
 * Day 리포지토리 구현체({@link DayRepositoryImpl}의 테스트 클래스입니다.
 * @see DayRepositoryImpl
 */
@Slf4j
@DisplayName("DayRepositoryImpl 테스트")
public class DayRepositoryImplTest extends RepositoryTest {

    /**
     * 테스트할 Day 리포지토리 구현체
     */
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

    /**
     * {@link DayRepositoryImpl#deleteAllByIds(List)} 실행 시
     * 전달받은 id의 Day들이 모두 삭제되는 지 테스트합니다.
     */
    @Test
    @DisplayName("deleteAllByIds 전달받은 Id 목록의 Day들을 삭제")
    void deleteAllByIdsTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 4);
        Trip trip = setupDecidedTrip(tripperId, startDate, endDate); // 여행 및 Day들 생성

        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        Day day3 = trip.getDays().get(2);
        Day day4 = trip.getDays().get(3);
        flushAndClear();

        // when
        dayRepository.deleteAllByIds(List.of(day1.getId(), day2.getId())); // day1, day2 삭제

        // then
        List<Day> remainingDays = findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId(), day4.getId()));

        assertThat(remainingDays.size()).isEqualTo(2);
        assertThat(remainingDays).map(Day::getId).containsExactlyInAnyOrder(day3.getId(), day4.getId()); // day3, day4만 남음
    }

    /**
     * {@link DayRepositoryImpl#deleteAllByTripId(Long)} 메서드 실행 시
     * 여행의 ID에 해당하는 Day를 모두 삭제할 수 있는 지 테스트합니다.
     * @see DayRepositoryImpl#deleteAllByTripId(Long)
     */
    @Test
    @DirtiesContext
    @DisplayName("deleteAllByTripId로 Day를 삭제하면, 해당 여행의 모든 Day들이 삭제된다.")
    void deleteAllByTripIdTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate); // 여행 생성, Day 생성, 영속화

        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        Day day3 = trip.getDays().get(2);
        flushAndClear();

        // when
        dayRepository.deleteAllByTripId(trip.getId()); // 모든 Day 삭제
        flushAndClear();

        // then
        List<Day> findDays = findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId()));
        assertThat(findDays).isEmpty(); // 조회 했을 때 Day 없음 검증
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

    /**
     * 전달받은 id들에 해당하는 Day들을 모두 조회합니다.
     * @param dayIds Day의 id들
     * @return 조회된 Day들
     */
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
