package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.infra.repository.TripRepositoryImpl;
import com.cosain.trilo.user.domain.User;
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
@DisplayName("TripRepositoryImpl 테스트")
public class TripRepositoryImplTest {

    @Autowired
    private TripRepositoryImpl tripRepositoryImpl;

    @Autowired
    private TestEntityManager em;

    @Nested
    @DisplayName("Trip 저장 후 FindById로 조회")
    class saveAndFindByIdTest {

        @Test
        @DisplayName("같은 id로 조회하면 같은 여행이 찾아진다.")
        void successTest() {
            Long tripperId = setupTripperId();
            Trip trip = Trip.create(TripTitle.of("제목"), tripperId);
            tripRepositoryImpl.save(trip);

            em.clear();

            Trip findTrip = tripRepositoryImpl.findById(trip.getId()).get();
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getTripperId()).isEqualTo(trip.getTripperId());
            assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());
        }

        @Test
        @DisplayName("임시보관함을 지연로딩(기본 양방향 매핑)하여 얻어오면, 순서대로 요소들이 가져와진다.")
        void lazy_loading_TemporaryStorage() {
            // given
            Long tripperId = setupTripperId();
            Trip trip = TripFixture.undecided_nullId(tripperId);
            tripRepositoryImpl.save(trip);

            Schedule schedule1 = ScheduleFixture.temporaryStorage_NullId(trip, 30_000_000L);
            Schedule schedule2 = ScheduleFixture.temporaryStorage_NullId(trip, 50_000_000L);
            Schedule schedule3 = ScheduleFixture.temporaryStorage_NullId(trip, -10_000_000L);

            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);

            em.clear();

            // when
            Trip findTrip = tripRepositoryImpl.findById(trip.getId()).get();
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
        @DisplayName("UnDecided 상태의 Trip을 조회하면 Trip만 조회된다.")
        public void findUndecidedTripTest() {
            // given
            Long tripperId = setupTripperId();
            Trip trip = TripFixture.undecided_nullId(tripperId);
            em.persist(trip);

            em.clear();

            // when
            Trip findTrip = tripRepositoryImpl.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getDays()).isEmpty();
        }

        @Test
        @DirtiesContext
        @DisplayName("findByIdWithDays -> Trip이 Day들을 가진 채 조회된다.")
        void testFindByIdWithDays(){
            // given
            Long tripperId = setupTripperId();
            LocalDate startDate = LocalDate.of(2023,5,2);
            LocalDate endDate = LocalDate.of(2023,5,3);

            Trip trip = setupDecidedTripAndPersist(tripperId, startDate, endDate);
            Day day1 = trip.getDays().get(0);
            Day day2 = trip.getDays().get(1);

            em.flush();
            em.clear();

            // when
            Trip findTrip = tripRepositoryImpl.findByIdWithDays(trip.getId()).get();

            // then
            assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
            assertThat(findTrip.getId()).isEqualTo(trip.getId());
            assertThat(findTrip.getDays().size()).isEqualTo(2);
            assertThat(findTrip.getDays()).map(Day::getTripDate).containsExactly(day1.getTripDate(), day2.getTripDate());
        }
    }

    @Test
    @DirtiesContext
    @DisplayName("delete 테스트")
    public void deleteTest() {
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTripAndPersist(tripperId);

        // when
        tripRepositoryImpl.delete(trip);
        em.flush();
        em.clear();

        // then
        Trip findTrip = tripRepositoryImpl.findById(trip.getId()).orElse(null);
        assertThat(findTrip).isNull();
    }

    @Nested
    class deleteAllByTripperIdTest{
        @Test
        void tripperId_에_해당하는_모든_trip이_제거된다(){
            // given
            Long tripperId = setupTripperId();
            Trip trip1 = setupUndecidedTripAndPersist(tripperId);
            Trip trip2 = setupUndecidedTripAndPersist(tripperId);
            Trip trip3 = setupUndecidedTripAndPersist(tripperId);
            Trip trip4 = setupUndecidedTripAndPersist(tripperId);

            em.flush();
            em.clear();

            // when
            tripRepositoryImpl.deleteAllByTripperId(tripperId);
            em.flush();
            em.clear();

            // then
            List<Trip> trips = tripRepositoryImpl.findAllByTripperId(tripperId);
            assertThat(trips).isEmpty();
        }
    }

    private Long setupTripperId() {
        User user = UserFixture.googleUser_NullId();
        em.persist(user);
        return user.getId();
    }

    private Trip setupUndecidedTripAndPersist(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    private Trip setupDecidedTripAndPersist(Long tripperId, LocalDate startDate, LocalDate endDate) {
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);
        return trip;
    }
}
