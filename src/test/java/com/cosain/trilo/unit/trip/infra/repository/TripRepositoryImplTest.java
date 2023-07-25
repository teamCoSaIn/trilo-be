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
import com.cosain.trilo.trip.domain.vo.TripStatus;
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

/**
 * 여행 리포지토리 구현체({@link TripRepositoryImpl}의 테스트 클래스입니다.
 * @see TripRepositoryImpl
 */
@RepositoryTest
@DisplayName("TripRepositoryImpl 테스트")
public class TripRepositoryImplTest {

    /**
     * 테스트할 여행 리포지토리 구현체
     */
    @Autowired
    private TripRepositoryImpl tripRepositoryImpl;

    /**
     * 영속성 컨텍스트
     */
    @Autowired
    private TestEntityManager em;

    /**
     * 여행을 저장 후 같은 id로 조회했을 때, 같은 여행이 찾아지는 지 검증
     * @see TripRepositoryImpl#save(Trip)
     * @see TripRepositoryImpl#findById(Long)
     */
    @Test
    @DisplayName("trip 저장 후 같은 id로 조회하면 같은 여행이 찾아진다.")
    void successTest() {
        // given
        Long tripperId = setupTripperId();
        Trip trip = Trip.create(TripTitle.of("제목"), tripperId);

        // when
        tripRepositoryImpl.save(trip);
        em.flush();
        em.clear();

        // then
        Trip findTrip = tripRepositoryImpl.findById(trip.getId()).orElseThrow(IllegalStateException::new); // 같은 id로 조회해 옴

        assertThat(findTrip.getId()).isEqualTo(trip.getId());
        assertThat(findTrip.getTripperId()).isEqualTo(trip.getTripperId());
        assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
        assertThat(findTrip.getTripPeriod()).isEqualTo(trip.getTripPeriod());
        assertThat(findTrip.getStatus()).isSameAs(trip.getStatus());
        assertThat(findTrip.getTripImage()).isEqualTo(trip.getTripImage()); // 필드가 동등한 지 검증
    }


    /**
     * 여행을 저장하고, 여행의 임시보관함 요소들을 지연 로딩을 통해 얻어왔을 때, 일정 순서 기준으로 오름차순으로 일정들이 가져와지는 지 검증합니다.
     * <ul>
     *     <li>임시보관함에서의 일정 순서값({@link ScheduleIndex})이 클 수록 뒤에 놓여야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("여행 저장 -> 임시보관함을 지연로딩(기본 양방향 매핑)하여 얻어오면, 일정 순서 기준 오름차순으로 일정들이 가져와진다.")
    void testTemporaryStorageLazyLoading() {
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTripAndPersist(tripperId);

        setupTemporaryScheduleAndPersist(trip, 30_000_000L);
        setupTemporaryScheduleAndPersist(trip, 50_000_000L);
        setupTemporaryScheduleAndPersist(trip, -10_000_000L);
        em.flush();
        em.clear();

        // when
        Trip findTrip = tripRepositoryImpl.findById(trip.getId()).orElseThrow(IllegalStateException::new); // 같은 id로 조회해옴
        List<Schedule> temporaryStorage = findTrip.getTemporaryStorage(); // 임시보관함을 지연로딩으로 불러옴

        // then
        assertThat(temporaryStorage).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(-10_000_000L, 30_000_000L, 50_000_000L); // 일정 순서값 기준 오름차순으로 가져와짐 검증
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

    /**
     * 저장소에 사용자를 저장하여 셋팅하고, 해당 사용자의 id를 발급받아 옵니다.
     * @return 새로 저장된 사용자의 id
     */
    private Long setupTripperId() {
        User user = UserFixture.googleUser_NullId();
        em.persist(user);
        return user.getId();
    }

    /**
     * {@link TripStatus#UNDECIDED} 상태의 여행을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @return 여행
     */
    private Trip setupUndecidedTripAndPersist(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    /**
     * {@link TripStatus#DECIDED} 상태의 여행 및 여행에 소속된 Day들을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @return 여행
     */
    private Trip setupDecidedTripAndPersist(Long tripperId, LocalDate startDate, LocalDate endDate) {
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);
        return trip;
    }

    /**
     * 임시보관함 일정을 생성 및 저장하여 셋팅하고 그 일정을 반환합니다.
     * @param trip 일정이 소속된 여행
     * @param scheduleIndexValue 일정의 순서값({@link ScheduleIndex})의 원시값({@link Long})
     * @return 일정
     */
    private Schedule setupTemporaryScheduleAndPersist(Trip trip, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }
}
