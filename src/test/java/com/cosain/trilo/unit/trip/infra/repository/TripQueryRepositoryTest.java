package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("TripQueryRepository 테스트")
public class TripQueryRepositoryTest {

    @Autowired
    private TripQueryRepository tripQueryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findTripDetailTest(){
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .title("제목")
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                .build();
        em.persist(trip);

        // when
        TripDetail tripDetail = tripQueryRepository.findTripDetailByTripId(1L).get();

        // then
        assertThat(tripDetail.getTitle()).isEqualTo(trip.getTitle());
        assertThat(tripDetail.getTripperId()).isEqualTo(trip.getTripperId());
        assertThat(tripDetail.getId()).isEqualTo(trip.getId());
        assertThat(tripDetail.getStartDate()).isEqualTo(trip.getTripPeriod().getStartDate());
        assertThat(tripDetail.getEndDate()).isEqualTo(trip.getTripPeriod().getEndDate());
        assertThat(tripDetail.getStatus()).isEqualTo(trip.getStatus().name());
    }

    @Nested
    @DisplayName("사용자의 여행 목록을 조회 하면")
    class findTripDetailListByTripperIdTest{

        @Test
        @DisplayName("여행의 TipperId가 일치하는 여행들이 요청된 페이지의 크기만큼 조회된다")
        void findTest(){
            // given
            Trip trip1 = Trip.builder()
                    .tripperId(1L)
                    .title("제목 1")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                    .build();

            Trip trip2 = Trip.builder()
                    .tripperId(1L)
                    .title("제목 2")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                    .build();


            em.persist(trip1);
            em.persist(trip2);

            // when
            Slice<TripSummary> tripSummariesByTripperId = tripQueryRepository.findTripSummariesByTripperId(1L, PageRequest.of(0, 2));

            // then
            assertThat(tripSummariesByTripperId.getContent().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("가장 최근에 생성된 여행 순으로 조회된다")
        void sortTest(){
            // given
            Trip trip1 = Trip.builder()
                    .tripperId(1L)
                    .title("제목 1")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                    .build();

            Trip trip2 = Trip.builder()
                    .tripperId(1L)
                    .title("제목 2")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                    .build();


            em.persist(trip1);
            em.persist(trip2);

            // when
            Slice<TripSummary> tripSummariesByTripperId = tripQueryRepository.findTripSummariesByTripperId(1L, PageRequest.of(0, 2));


            // then
            assertThat(tripSummariesByTripperId.getContent().get(0).getTitle()).isEqualTo(trip2.getTitle());
            assertThat(tripSummariesByTripperId.getContent().get(1).getTitle()).isEqualTo(trip1.getTitle());

        }

        @Test
        void existByIdTest(){
            // given
            Trip trip = Trip.builder()
                    .tripperId(1L)
                    .title("제목 1")
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10)))
                    .build();
            em.persist(trip);
            em.flush();

            // when & then
            long notExistTripId = 2L;
            assertThat(tripQueryRepository.existById(trip.getId())).isTrue();
            assertThat(tripQueryRepository.existById(notExistTripId)).isFalse();
        }

    }


}
