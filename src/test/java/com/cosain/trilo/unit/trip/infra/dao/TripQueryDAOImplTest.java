
package com.cosain.trilo.unit.trip.infra.dao;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.dao.TripQueryDAOImpl;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TripQueryDAOImpl 테스트")
public class TripQueryDAOImplTest extends RepositoryTest {

    @Autowired
    private TripQueryDAOImpl tripQueryDAOImpl;

    @Autowired
    private EntityManager em;

    @Test
    @DirtiesContext
    void findTripDetailTest() {
        // given
        Long tripperId = setupTripperId();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 2);

        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);

        // when
        TripDetail tripDetail = tripQueryDAOImpl.findTripDetailById(1L).get();

        // then
        assertThat(tripDetail.getTitle()).isEqualTo(trip.getTripTitle().getValue());
        assertThat(tripDetail.getTripperId()).isEqualTo(trip.getTripperId());
        assertThat(tripDetail.getTripId()).isEqualTo(trip.getId());
        assertThat(tripDetail.getStartDate()).isEqualTo(trip.getTripPeriod().getStartDate());
        assertThat(tripDetail.getEndDate()).isEqualTo(trip.getTripPeriod().getEndDate());
        assertThat(tripDetail.getStatus()).isEqualTo(trip.getStatus().name());
    }

    @Nested
    @DisplayName("사용자의 여행 목록을 조회 하면")
    class findTripDetailListByTripperIdTest {

        @Test
        @DirtiesContext
        @DisplayName("여행의 TipperId가 일치하는 여행들이 커서에 해당하는 tripId 미만 row가 size 만큼 조회된다")
        void findTest() {
            // given
            Long tripperId = setupTripperId();

            Trip trip1 = TripFixture.undecided_nullId(tripperId);
            Trip trip2 = TripFixture.undecided_nullId(tripperId);
            em.persist(trip1);
            em.persist(trip2);

            System.out.printf("trip ids = [%d, %d]%n", trip1.getId(), trip2.getId());
            em.flush();
            em.clear();

            int pageSize = 2;
            Long tripId = trip2.getId() + 1L;
            TripListQueryParam queryParam = TripListQueryParam.of(tripperId, tripId, pageSize);

            // when
            TripListSearchResult tripListSearchResult = tripQueryDAOImpl.findTripSummariesByTripperId(queryParam);

            // then
            assertThat(tripListSearchResult.getTrips().size()).isEqualTo(2);
        }

        @Test
        @DirtiesContext
        @DisplayName("가장 최근에 생성된 여행 순으로 조회된다")
        void sortTest() {
            // given
            Long tripperId = setupTripperId();
            int pageSize = 3;

            Trip trip1 = TripFixture.undecided_nullId(tripperId);
            Trip trip2 = TripFixture.undecided_nullId(tripperId);
            em.persist(trip1);
            em.persist(trip2);
            em.flush();
            em.clear();

            Long tripId = trip2.getId() + 1L;
            TripListQueryParam queryParam = TripListQueryParam.of(tripperId, tripId, pageSize);


            // when
            TripListSearchResult searchResult = tripQueryDAOImpl.findTripSummariesByTripperId(queryParam);


            // then
            assertThat(searchResult.getTrips().get(0).getTitle()).isEqualTo(trip2.getTripTitle().getValue());
            assertThat(searchResult.getTrips().get(1).getTitle()).isEqualTo(trip1.getTripTitle().getValue());
        }

        @Test
        @DirtiesContext
        void existByIdTest() {
            // given
            Long tripperId = setupTripperId();

            Trip trip = TripFixture.undecided_nullId(tripperId);
            em.persist(trip);
            em.flush();
            em.clear();

            // when & then
            long notExistTripId = 2L;
            assertThat(tripQueryDAOImpl.existById(trip.getId())).isTrue();
            assertThat(tripQueryDAOImpl.existById(notExistTripId)).isFalse();
        }

    }

    @Nested
    class 사용자_여행_통계_조회{
        @Test
        void 총_여행_개수와_종료된_여행_개수를_반환한다(){
            // given
            Long tripperId = setupTripperId();
            LocalDate today = LocalDate.of(2023, 4, 28);
            Trip terminatedTrip1 = TripFixture.decided_nullId(tripperId, today.minusDays(3), today.minusDays(1));
            Trip terminatedTrip2 = TripFixture.decided_nullId(tripperId, today.minusDays(3), today.minusDays(1));
            Trip terminatedTrip3 = TripFixture.decided_nullId(tripperId, today.minusDays(3), today.minusDays(1));
            Trip unTerminatedTrip1 = TripFixture.decided_nullId(tripperId, today.plusDays(1), today.plusDays(3));
            Trip unTerminatedTrip2 = TripFixture.decided_nullId(tripperId, today.plusDays(1), today.plusDays(3));

            em.persist(terminatedTrip1);
            em.persist(terminatedTrip2);
            em.persist(terminatedTrip3);
            em.persist(unTerminatedTrip1);
            em.persist(unTerminatedTrip2);


            // when
            TripStatistics tripStatistics = tripQueryDAOImpl.findTripStaticsByTripperId(tripperId, today);

            // then
            assertThat(tripStatistics.getTerminatedTripCnt()).isEqualTo(3);
            assertThat(tripStatistics.getTotalTripCnt()).isEqualTo(5);
        }
    }

    @Nested
    class 여행_조건_조회{
        @Test
        void 기본_또는_최신순_조회(){
            // given
            TripSearchRequest tripSearchRequest = new TripSearchRequest("제주", "RECENT", 5, null);
            Long tripperId = setupTripperId();
            Long tripperId2 = setupTripperId();
            LocalDate startDate = LocalDate.of(2023, 5, 1);
            LocalDate endDate = LocalDate.of(2023, 5, 10);
            Trip trip1 = TripFixture.decided_nullId_Title(tripperId,"제주도 여행",startDate, endDate);
            Trip trip2 = TripFixture.decided_nullId_Title(tripperId2,"재미있는 제주 1박 2일!",startDate, endDate);
            Trip trip3 = TripFixture.decided_nullId_Title(tripperId,"여행을 가보자",startDate, endDate);
            Trip trip4 = TripFixture.decided_nullId_Title(tripperId,"제 주 도",startDate, endDate);
            Trip trip5 = TripFixture.decided_nullId_Title(tripperId,"제 주 도 가자",startDate, endDate);
            Trip trip6 = TripFixture.decided_nullId_Title(tripperId2,"밤샘 여행",startDate, endDate);
            Trip trip7 = TripFixture.decided_nullId_Title(tripperId2,"제조여행",startDate, endDate);

            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);
            em.persist(trip4);
            em.persist(trip5);
            em.persist(trip6);
            em.persist(trip7);

            // when
            TripSearchResponse response = tripQueryDAOImpl.findWithSearchConditions(tripSearchRequest);

            // then
            List<TripSearchResponse.TripSummary> trips = response.getTrips();
            assertThat(trips.size()).isEqualTo(2);
        }
    }

}
