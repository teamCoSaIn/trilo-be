package com.cosain.trilo.unit.trip.query.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
