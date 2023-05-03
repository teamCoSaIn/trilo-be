package com.cosain.trilo.unit.trip.query.infra.repository;

import com.cosain.trilo.config.QueryDslConfig;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.infra.repository.trip.TripQueryDslRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslConfig.class, TripQueryDslRepository.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("TripQueryDslRepository 테스트")
public class TripQueryRepositoryImplTest {

    @Autowired
    private TripQueryDslRepository tripQueryDslRepository;

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
        TripDetail tripDetail = tripQueryDslRepository.findTripDetailById(1L).get();

        // then
        assertThat(tripDetail.getTitle()).isEqualTo(trip.getTitle());
        assertThat(tripDetail.getTripperId()).isEqualTo(trip.getTripperId());
        assertThat(tripDetail.getId()).isEqualTo(trip.getId());
        assertThat(tripDetail.getStartDate()).isEqualTo(trip.getTripPeriod().getStartDate());
        assertThat(tripDetail.getEndDate()).isEqualTo(trip.getTripPeriod().getEndDate());
        assertThat(tripDetail.getStatus()).isEqualTo(trip.getStatus().name());
    }
}
