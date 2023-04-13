package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
}
