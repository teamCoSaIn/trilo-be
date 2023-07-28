package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.infra.repository.TripRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 여행 리포지토리 구현체({@link TripRepositoryImpl}의 테스트 클래스입니다.
 * @see TripRepositoryImpl
 */
@DisplayName("TripRepositoryImpl 테스트")
public class TripRepositoryImplTest extends RepositoryTest {

    /**
     * 테스트할 여행 리포지토리 구현체
     */
    @Autowired
    private TripRepositoryImpl tripRepositoryImpl;

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
        flushAndClear();

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
        Trip trip = setupUndecidedTrip(tripperId);

        setupTemporarySchedule(trip, 30_000_000L);
        setupTemporarySchedule(trip, 50_000_000L);
        setupTemporarySchedule(trip, -10_000_000L);
        flushAndClear();

        // when
        Trip findTrip = tripRepositoryImpl.findById(trip.getId()).orElseThrow(IllegalStateException::new); // 같은 id로 조회해옴
        List<Schedule> temporaryStorage = findTrip.getTemporaryStorage(); // 임시보관함을 지연로딩으로 불러옴

        // then
        assertThat(temporaryStorage).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(-10_000_000L, 30_000_000L, 50_000_000L); // 일정 순서값 기준 오름차순으로 가져와짐 검증
    }


    /**
     * 여행과 여행의 Day들을 함께 가져올 때 잘 가져와지는 지 테스트합니다.
     * @see TripRepositoryImpl#findByIdWithDays(Long)
     */
    @Test
    @DirtiesContext
    @DisplayName("findByIdWithDays 테스트")
    void testFindByIdWithDays(){
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023,5,2);
        LocalDate endDate = LocalDate.of(2023,5,3);

        Trip trip = setupDecidedTrip(tripperId, startDate, endDate);
        Day day1 = trip.getDays().get(0);
        Day day2 = trip.getDays().get(1);
        flushAndClear(); // trip 및 day들 저장

        // when
        Trip findTrip = tripRepositoryImpl.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);

        // then
        assertThat(findTrip.getTripTitle()).isEqualTo(trip.getTripTitle());
        assertThat(findTrip.getId()).isEqualTo(trip.getId());
        assertThat(findTrip.getDays().size()).isEqualTo(2); // Day들도 같이 잘 가져와짐
        assertThat(findTrip.getDays()).map(Day::getTripDate).containsExactly(day1.getTripDate(), day2.getTripDate());
    }

    /**
     * 여행을 삭제 후, 같은 id로 조회했을 때 같은 여행이 찾아지는 지 검증
     * @see TripRepositoryImpl#delete(Trip)
     * @see TripRepositoryImpl#findById(Long)
     */
    @Test
    @DirtiesContext
    @DisplayName("delete 테스트")
    public void deleteTest() {
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTrip(tripperId);

        // when
        tripRepositoryImpl.delete(trip); // 여행 삭제
        flushAndClear();

        // then
        assertThat(tripRepositoryImpl.findById(trip.getId())).isEmpty(); // 같은 id로 찾았을 때 여행 없음
    }

    @Nested
    class deleteAllByTripperIdTest{
        @Test
        void tripperId_에_해당하는_모든_trip이_제거된다(){
            // given
            Long tripperId = setupTripperId();
            Trip trip1 = setupUndecidedTrip(tripperId);
            Trip trip2 = setupUndecidedTrip(tripperId);
            Trip trip3 = setupUndecidedTrip(tripperId);
            Trip trip4 = setupUndecidedTrip(tripperId);
            flushAndClear();

            // when
            tripRepositoryImpl.deleteAllByTripperId(tripperId);
            flushAndClear();

            // then
            List<Trip> trips = tripRepositoryImpl.findAllByTripperId(tripperId);
            assertThat(trips).isEmpty();
        }
    }
}
